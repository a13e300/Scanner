package io.github.a13e300.scanner.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.a13e300.scanner.QRCode
import io.github.a13e300.scanner.R
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
        val qrcode = QRCode(content, 500, 500)
        qrcode.drawLogo(BitmapFactory.decodeResource(resources, R.drawable.njupt_logo))
        binding.image.setImageBitmap(qrcode.getImage())
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