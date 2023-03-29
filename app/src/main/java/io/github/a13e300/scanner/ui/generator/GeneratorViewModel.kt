package io.github.a13e300.scanner.ui.generator

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.a13e300.scanner.QRCode
import io.github.a13e300.scanner.R

data class QRCodeInfo(
    val content: String = ""
)

class GeneratorViewModel(application: Application) : AndroidViewModel(application) {
    val image: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }
    val info: MutableLiveData<QRCodeInfo> by lazy {
        MutableLiveData<QRCodeInfo>(QRCodeInfo())
    }

    fun updateQRCode() {
        val content = info.value?.content
        if (content?.isNotEmpty() == true) {
            val qrcode = QRCode(content, 500, 500)
            qrcode.drawLogo(
                BitmapFactory.decodeResource(
                    getApplication<Application>().resources,
                    R.drawable.njupt_logo
                )
            )
            image.value = qrcode.getImage()
        } else {
            image.value = null
        }
    }
}