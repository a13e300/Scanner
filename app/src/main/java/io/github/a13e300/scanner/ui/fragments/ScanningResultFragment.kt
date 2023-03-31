package io.github.a13e300.scanner.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentScanningResultBinding

class ScanningResultFragment : BaseFragment() {
    private var result: String? = null

    companion object {
        private const val ARG_RESULT = "result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getString(ARG_RESULT)
        }
    }

    override fun onCreateContent(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentScanningResultBinding.inflate(inflater, container, false)
        binding.apply {
            // FIXME: autoLink bug
            resultText.text = result
            copyResult.setOnClickListener {
                val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("", result))
                showSnackBar(getString(R.string.tips_copied))
            }
            shareResult.setOnClickListener {
                startActivity(Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, result)
                    }, getString(R.string.title_scan_result)
                ))
            }
        }
        return binding.root
    }
}