package io.github.a13e300.scanner.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.a13e300.scanner.Licenses
import io.github.a13e300.scanner.databinding.FragmentLicensesListBinding
import io.github.a13e300.scanner.ui.misc.openWebLink

class LicensesFragment : BaseFragment(), LicenseItemAdapter.OnItemClickListener {

    override fun onClick(url: String?) {
        url?.also { openWebLink(Uri.parse(it)) }
    }

    override fun onCreateContent(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLicensesListBinding.inflate(inflater, container, false)
        with (binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = LicenseItemAdapter(Licenses.ITEMS).apply { onItemClickListener = this@LicensesFragment }
        }
        return binding.root
    }
}