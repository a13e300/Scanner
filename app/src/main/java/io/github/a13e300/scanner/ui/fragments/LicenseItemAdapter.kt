package io.github.a13e300.scanner.ui.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.a13e300.scanner.Licenses.LicenseItem
import io.github.a13e300.scanner.databinding.FragmentLicensesBinding

class LicenseItemAdapter(
    private val values: List<LicenseItem>
) : RecyclerView.Adapter<LicenseItemAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(url: String?)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        onItemClickListener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            FragmentLicensesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.apply {
            title.text = item.projectName
            content.text = "${item.projectLink}\n${item.license}"
            root.setOnClickListener {
                onItemClickListener?.onClick(item.projectLink)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLicensesBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.projectName
        val content: TextView = binding.licenseDetail
        val root: View = binding.root
    }

}