package io.github.a13e300.scanner.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.ui.misc.openWebLink

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        findPreference<Preference>("project_link")?.apply {
            setOnPreferenceClickListener {
                openWebLink(Uri.parse("https://github.com/a13e300/Scanner"))
                true
            }
        }
        findPreference<Preference>("licenses")?.apply {
            setOnPreferenceClickListener {
                findNavController().navigate(R.id.show_licenses)
                true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val newContainer = view.findViewById<ViewGroup>(R.id.license_detail)
        newContainer.addView(super.onCreateView(inflater, newContainer, savedInstanceState))
        return view
    }
}