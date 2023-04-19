package io.github.a13e300.scanner.ui.models

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.a13e300.scanner.QRCode
import io.github.a13e300.scanner.R
import java.io.File

// GeneratorViewModel

data class QRCodeInfo(
    val content: String = "",
    val icon: Bitmap? = null
)

class GeneratorViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val FILE_CUSTOM_ICON = "custom_icon"
    }
    val customIconFile = File(application.filesDir, FILE_CUSTOM_ICON)
    // 最终显示的二维码图像 Bitmap
    val image: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }
    // 最终决定二维码结果的信息，监听该 LiveData 并绘制二维码，更新 image
    val info: MutableLiveData<QRCodeInfo> by lazy {
        MutableLiveData<QRCodeInfo>(QRCodeInfo())
    }
    // 图标类型，对应按钮的 id
    val iconType: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(R.id.icon_default)
    }
    val customIcon: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>(null)
    }

    fun loadCustomIcon(): Boolean {
        // 加载自定义图标文件
        try {
            customIcon.value = BitmapFactory.decodeFile(customIconFile.absolutePath)!!
            return true
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return false
    }

    fun clearCustomIcon() {
        // 删除自定义图标文件
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
            // 存在要编码的内容
            val qrcode = QRCode(content, 500, 500)
            info.icon?.let { icon ->
                qrcode.drawLogo(icon)
            }
            // 更新 LiveData
            image.value = qrcode.getImage()
        } else {
            // 不存在
            image.value = null
        }
    }
}