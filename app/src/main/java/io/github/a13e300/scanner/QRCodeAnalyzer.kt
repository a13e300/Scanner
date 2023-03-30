package io.github.a13e300.scanner

import android.graphics.ImageFormat
import android.os.Build
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QRCodeAnalyzer(
    private val isScanning: MutableLiveData<Boolean>,
    private val scanResult: MutableLiveData<String?>
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = mutableListOf(
        ImageFormat.YUV_420_888
    ).also {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            it.add(ImageFormat.YUV_422_888)
            it.add(ImageFormat.YUV_444_888)
        }
    }

    override fun analyze(image: ImageProxy) {
        if (isScanning.value == true && image.format in supportedImageFormats) {
            val bytes = image.planes.first().buffer.toByteArray()
            image.imageInfo.rotationDegrees
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE,
                            )
                        )
                    )
                }.decodeWithState(binaryBmp)
                scanResult.postValue(result.text)
            } catch (e: NotFoundException) {
                // ignore
            } finally {
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}
