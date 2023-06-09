package io.github.a13e300.scanner.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentScanningResultBinding
import io.github.a13e300.scanner.ui.misc.checkUrl
import io.github.a13e300.scanner.ui.misc.openWebLink

class ScanningResultFragment : BaseFragment() {
    private var result: String? = null

    companion object {
        private const val ARG_RESULT = "result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getString(ARG_RESULT)
            // 自动打开链接（需要偏好中开启）
            if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("auto_open_links", false)) {
                result?.also {
                    val url = checkUrl(it) ?: return@also
                    Log.d("FIVECC", "auto open url $url")
                    openWebLink(url)
                }
            }
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