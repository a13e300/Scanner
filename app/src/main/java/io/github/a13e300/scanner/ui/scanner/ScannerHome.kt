package io.github.a13e300.scanner.ui.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentScannerHomeBinding

class ScannerHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentScannerHomeBinding.inflate(inflater, container, false)
        binding.scanWithCameraButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_scanner_home_to_navigation_camera_scan)
        }
        return binding.root
    }
}