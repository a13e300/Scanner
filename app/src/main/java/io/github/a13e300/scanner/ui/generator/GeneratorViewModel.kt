package io.github.a13e300.scanner.ui.generator

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.a13e300.scanner.QRCode

data class QRCodeInfo(
    val content: String = "",
    val icon: Bitmap? = null
)

class GeneratorViewModel(application: Application) : AndroidViewModel(application) {
    val image: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }
    val info: MutableLiveData<QRCodeInfo> by lazy {
        MutableLiveData<QRCodeInfo>(QRCodeInfo())
    }

    fun updateQRCode() {
        val info = info.value ?: return
        val content = info.content
        if (content.isNotEmpty() == true) {
            val qrcode = QRCode(content, 500, 500)
            info.icon?.let { icon ->
                qrcode.drawLogo(icon)
            }
            image.value = qrcode.getImage()
        } else {
            image.value = null
        }
    }
}