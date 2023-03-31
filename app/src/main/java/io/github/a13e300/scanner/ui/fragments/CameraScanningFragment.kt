package io.github.a13e300.scanner.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.github.a13e300.scanner.QRCodeAnalyzer
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentCameraScanningBinding
import io.github.a13e300.scanner.ui.models.ScannerModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraScanningFragment : BaseFragment() {

    companion object {
        private const val TAG = "Scanner"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (allPermissionsGranted())
            startCamera()
        else {
            showSnackBar(getString(R.string.tips_permission_required))
            // TODO: show this in home fragment
            view?.postDelayed({
                findNavController().navigateUp()
            }, 1000)
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
            if (result != null) {
                if (viewModel.isScanning.value == true) {
                    viewModel.isScanning.value = false
                    findNavController().navigate(R.id.action_navigation_camera_scan_to_navigation_scanning_result,
                        bundleOf("result" to result)
                    )
                }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateContent(
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

