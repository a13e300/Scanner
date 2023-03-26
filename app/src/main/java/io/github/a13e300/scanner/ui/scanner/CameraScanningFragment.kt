package io.github.a13e300.scanner.ui.scanner

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import io.github.a13e300.scanner.QRCodeAnalyzer
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentCameraScanningBinding
import io.github.a13e300.scanner.databinding.FragmentScanResultBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraScanningFragment : Fragment() {

    companion object {
        private const val TAG = "Scanner"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (allPermissionsGranted())
            startCamera()
        else {
            Snackbar.make(
                binding.root,
                getString(R.string.tips_permission_required),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private var _binding: FragmentCameraScanningBinding? = null

    private lateinit var cameraExecutor: ExecutorService

    private val viewModel: ScannerModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer(viewModel.isScanning, viewModel.scanResult))
                }


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        viewModel.scanResult.observe(this) { result ->
            Log.d(TAG, "scanResult=$result")

            if (result != null) {
                if (viewModel.isScanning.value == true) {
                    viewModel.isScanning.value = false
                    // val resultDialog = ResultFragment()
                    // resultDialog.show(childFragmentManager, "Result")
                    findNavController().navigate(R.id.action_navigation_camera_scan_to_navigation_scanning_result,
                        bundleOf("result" to result)
                    )
                }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraScanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ResultFragment: BottomSheetDialogFragment() {
    private val viewModel: ScannerModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentScanResultBinding.inflate(inflater, container, false)
        viewModel.scanResult.observe(this) {
            binding.resultContent.text = it
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.isScanning.value = true
        viewModel.scanResult.value = null
        Log.d("Result", "dismissed")
    }
}
