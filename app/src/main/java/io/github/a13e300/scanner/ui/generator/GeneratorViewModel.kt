package io.github.a13e300.scanner.ui.generator

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.a13e300.scanner.QRCode
import io.github.a13e300.scanner.R

class GeneratorViewModel(application: Application) : AndroidViewModel(application) {
    val image: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }
    val content: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>(null)
    }

    fun updateQRCode() {
        val content = content.value
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