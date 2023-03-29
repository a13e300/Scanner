package io.github.a13e300.scanner

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class QRCode(value: String, val width: Int, val height: Int) {
    companion object {
        private const val bgColor = 0xffffffffu
        private const val fgColor = 0xff000000u

        fun createBitmap(matrix: BitMatrix): Bitmap {
            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = (if (matrix[x, y]) fgColor else bgColor).toInt()
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        }
    }

    var mBitmap: Bitmap

    init {
        val writer = MultiFormatWriter()
        val hints = mutableMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        /*
         * 设置容错级别，默认为ErrorCorrectionLevel.L
         * 因为中间加入logo所以建议你把容错级别调至H,否则可能会出现识别不了
         */
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        // 设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 1 //default is 4
        // 图像数据转换，使用了矩阵转换
        val matrix = writer.encode(value, BarcodeFormat.QR_CODE, width, height, hints)
        mBitmap = createBitmap(matrix)
    }

    fun drawLogo(logo: Bitmap) {
        val srcWidth = width
        val srcHeight = height
        val logoWidth = logo.width
        val logoHeight = logo.height
        // logo大小为二维码整体大小的1/5
        // TODO: calculate logo size
        val scaleFactor = srcWidth * 0.2f / logoWidth
        val canvas = Canvas(mBitmap)
        canvas.scale(
            scaleFactor,
            scaleFactor,
            (srcWidth / 2).toFloat(),
            (srcHeight / 2).toFloat()
        )
        val left = ((srcWidth - logoWidth) / 2).toFloat()
        val top = ((srcHeight - logoHeight) / 2).toFloat()
        val paint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            setShadowLayer(5f, 0f, 0f, Color.BLACK)
        }
        canvas.drawRoundRect(left,  top, left + logoWidth,top + logoHeight, (logoWidth / 5).toFloat(), (logoHeight / 5).toFloat(), paint)
        canvas.drawBitmap(logo, left, top, null)
        canvas.save()
        canvas.restore()
    }

    fun getImage() = mBitmap

}