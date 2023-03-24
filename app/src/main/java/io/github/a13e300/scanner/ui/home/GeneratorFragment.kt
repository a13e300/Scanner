package io.github.a13e300.scanner.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.Utils
import io.github.a13e300.scanner.databinding.FragmentGeneratorBinding

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun generateQRCode(content: String) {
        val writer = MultiFormatWriter()
        val hints = mutableMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        /*
         * 设置容错级别，默认为ErrorCorrectionLevel.L
         * 因为中间加入logo所以建议你把容错级别调至H,否则可能会出现识别不了
         */
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 1 //default is 4
        //图像数据转换，使用了矩阵转换
        val matrix = writer.encode(content, BarcodeFormat.QR_CODE, 350, 350, hints)
        var bitmap = Utils.createBitmap(matrix)
        val logo = BitmapFactory.decodeResource(resources, R.drawable.njupt_logo)
        bitmap = Utils.addLogo(bitmap, logo)!!
        binding.image.setImageBitmap(bitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateQRCode("hello")
        binding.button.setOnClickListener {
            generateQRCode(binding.editText.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}