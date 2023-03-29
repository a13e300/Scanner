package io.github.a13e300.scanner.ui.generator

import android.app.Dialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.WriterException
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.StorageUtils
import io.github.a13e300.scanner.databinding.FragmentContentInputBinding
import io.github.a13e300.scanner.databinding.FragmentGeneratorBinding
import io.github.a13e300.scanner.parcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneratorFragment : Fragment() {
    companion object {
        private const val REQUEST_CROP_FOR_ICON = "crop_for_icon"
    }

    private var _binding: FragmentGeneratorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<GeneratorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            StorageUtils.verifyStoragePermissions(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.qr_generator_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.save_qr_code) {
                    saveImage()
                }
                return true
            }

        }, viewLifecycleOwner)
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun saveImage() {
        viewModel.image.value?.also { img ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    StorageUtils.saveImages(requireContext(), img)
                }
                showSnackBar(getString(R.string.tips_saved_to_gallery))
            }
        }
    }

    private fun showSnackBar(msg: String) {
        // TODO: create a function to show snackbar in ALL fragments
        Snackbar.make(
            binding.root,
            msg,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(R.id.nav_view).show()
    }

    private val pickCustomIconLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        findNavController().navigate(R.id.crop_for_icon,
            bundleOf("uri" to ImageCropRequest(
                it, REQUEST_CROP_FOR_ICON, 100, viewModel.customIconFile.toUri()
            ))
        )
        setFragmentResultListener(REQUEST_CROP_FOR_ICON) { _, data ->
            val result = data.parcelable<Uri>(ImageCropFragment.ARG_RESULT)
            if (viewModel.loadCustomIcon()) {
                viewModel.iconType.value = R.id.icon_custom
            }
        }
    }

    private fun updateLogo(id: Int, oldId: Int) {
        if (id == R.id.icon_custom) {
            if (!viewModel.loadCustomIcon()) {
                binding.iconGroup.check(oldId)
                pickCustomIconLauncher.launch("image/*")
                return
            }
        }
        viewModel.iconType.value = id
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.info.observe(viewLifecycleOwner) {
            binding.editText.setText(it.content)
            try {
                viewModel.updateQRCode()
            } catch (e: WriterException) {
                e.printStackTrace()
                showSnackBar(getString(R.string.qr_code_content_too_long))
                viewModel.info.value = viewModel.info.value!!.copy(content = "")
            }
        }
        viewModel.image.observe(viewLifecycleOwner) {
            binding.image.setImageBitmap(it)
        }
        binding.editText.apply {
            setText(viewModel.info.value?.content)
            setOnClickListener {
                ContentInputFragment().show(parentFragmentManager, "dialog")
            }
        }
        viewModel.iconType.apply {
            observe(viewLifecycleOwner) {
                binding.iconGroup.check(it)
                val bitmap = when (it) {
                    R.id.icon_default -> {
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.default_logo
                        )
                    }
                    R.id.icon_custom -> {
                        viewModel.customIcon.value
                    }
                    else -> null
                }
                binding.previewIcon.apply {
                    isGone = bitmap == null
                    setImageBitmap(bitmap)
                }
                viewModel.info.value = viewModel.info.value!!.copy(icon = bitmap)
                binding.iconGroup.check(it)
            }
        }
        binding.previewIcon.setOnClickListener {
            if (viewModel.iconType.value == R.id.icon_custom) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.custom_icon_title)
                    .setPositiveButton(R.string.custom_icon_change) { _, _ ->
                       pickCustomIconLauncher.launch("image/*")
                    }
                    .setNegativeButton(R.string.custom_icon_clear) { _, _ ->
                        viewModel.clearCustomIcon()
                    }
                    .setNeutralButton(R.string.cancel, null)
                    .create().show()
            }
        }
        binding.iconGroup.addOnButtonCheckedListener { _, id, isChecked ->
            val oldId = viewModel.iconType.value!!
            if (isChecked && oldId != id) updateLogo(id, oldId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ContentInputFragment : DialogFragment() {
    private val viewModel by activityViewModels<GeneratorViewModel>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // FIXME: textInput disappearedwhen soft input shown on API 22
        val binding = FragmentContentInputBinding.inflate(requireActivity().layoutInflater, null, false)
        viewModel.info.observe(this) {
            binding.editText.setText(it.content)
        }
        binding.editText.requestFocus()
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.qr_code_content))
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.info.value = viewModel.info.value!!.copy(content = binding.editText.text.toString())
            }
            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
    }
}
