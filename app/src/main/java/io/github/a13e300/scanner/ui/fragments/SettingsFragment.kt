package io.github.a13e300.scanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import io.github.a13e300.scanner.databinding.FragmentSettingsBinding
import io.github.a13e300.scanner.ui.models.SettingsViewModel

class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateContent(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}