package io.github.a13e300.scanner.ui.generator

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.a13e300.scanner.QRCode
import io.github.a13e300.scanner.R
import java.io.File

data class QRCodeInfo(
    val content: String = "",
    val icon: Bitmap? = null
)

class GeneratorViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val FILE_CUSTOM_ICON = "custom_icon"
    }
    val customIconFile = File(application.filesDir, FILE_CUSTOM_ICON)
    val image: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }
    val info: MutableLiveData<QRCodeInfo> by lazy {
        MutableLiveData<QRCodeInfo>(QRCodeInfo())
    }
    val iconType: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(R.id.icon_default)
    }
    val customIcon: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }

    fun loadCustomIcon(): Boolean {
        try {
            customIcon.value = BitmapFactory.decodeFile(customIconFile.absolutePath)!!
            return true
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return false
    }

    fun clearCustomIcon() {
        kotlin.runCatching {
            customIconFile.delete()
        }.onFailure { it.printStackTrace() }
        iconType.value = R.id.icon_none
        customIcon.value = null
    }

    fun updateQRCode() {
        val info = info.value ?: return
        val content = info.content
        if (content.isNotEmpty()) {
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