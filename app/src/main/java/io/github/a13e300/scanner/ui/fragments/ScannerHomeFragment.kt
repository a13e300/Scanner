package io.github.a13e300.scanner.ui.fragments

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentScannerHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerHomeFragment : BaseFragment() {

    private val pickImageFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent(), this::scanFromFile)

    override fun onCreateContent(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentScannerHomeBinding.inflate(inflater, container, false)
        binding.scanWithCameraButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_scanner_home_to_navigation_camera_scan)
        }
        binding.scanFromFileButton.setOnClickListener {
            pickImageFileLauncher.launch("image/*")
        }
        return binding.root
    }

    private fun scanFromFile(uri: Uri?) {
        if (uri == null) {
            showSnackBar(getString(R.string.qr_code_cancelled))
            return
        }
        // TODO: allow user to crop image before scanning
        // TODO: improve scan ability
        lifecycleScope.launch {
            var resultText: String? = null
            withContext(Dispatchers.IO) {
                val bitmap = requireContext().contentResolver.openInputStream(uri).use {
                    BitmapFactory.decodeStream(it)
                }
                val width = bitmap.width
                val height = bitmap.height
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                bitmap.recycle()
                val source = RGBLuminanceSource(width, height, pixels)
                val binBitmap = BinaryBitmap(HybridBinarizer(source))
                try {
                    resultText = MultiFormatReader().apply {
                        setHints(
                            mapOf(
                                DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                    BarcodeFormat.QR_CODE,
                                )
                            )
                        )
                    }.decodeWithState(binBitmap).text
                } catch (e: NotFoundException) {
                    // ignore
                }
            }
            if (resultText != null) {
                findNavController().navigate(R.id.navigation_scanning_result,
                    bundleOf("result" to resultText)
                )
            } else {
                showSnackBar(getString(R.string.qr_code_not_found))
            }
        }
    }
}