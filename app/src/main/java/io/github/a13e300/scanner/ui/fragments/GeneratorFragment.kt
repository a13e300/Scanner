package io.github.a13e300.scanner.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.WriterException
import io.github.a13e300.scanner.BuildConfig
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentContentInputBinding
import io.github.a13e300.scanner.databinding.FragmentGeneratorBinding
import io.github.a13e300.scanner.ui.misc.DoneMenuProvider
import io.github.a13e300.scanner.ui.misc.StorageUtils
import io.github.a13e300.scanner.ui.models.GeneratorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GeneratorFragment : BaseFragment() {
    companion object {
        private const val REQUEST_CROP_FOR_ICON = "crop_for_icon"
        private const val SHARE_PATH = "share/share.png"
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

    override fun onSetMenuProvider(): MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.qr_generator_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.save_qr_code -> {
                    saveImage()
                    true
                }
                R.id.share_qr_code -> {
                    shareImage()
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateContent(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private fun shareImage() {
        viewModel.image.value?.also { img ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val ctx = requireContext()
                    val file = File(ctx.filesDir, SHARE_PATH)
                    file.parentFile?.mkdirs()
                    StorageUtils.writeBitmap(img, file.outputStream())
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(
                            Intent.EXTRA_STREAM,
                            FileProvider.getUriForFile(ctx, "${BuildConfig.APPLICATION_ID}.fp", file)
                        )
                        type = "image/png"
                    }.let { Intent.createChooser(it, getString(R.string.share)) }
                    startActivity(intent)
                }
            }
        }
    }

    private val pickCustomIconLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        findNavController().navigate(R.id.crop_for_icon,
            bundleOf("uri" to ImageCropRequest(
                it, REQUEST_CROP_FOR_ICON, 100, viewModel.customIconFile.toUri()
            )
            )
        )
        setFragmentResultListener(REQUEST_CROP_FOR_ICON) { _, data ->
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

    private fun updatePreviewContentText() {
        binding.editText.apply {
            setText(viewModel.info.value?.content)
            inputType = InputType.TYPE_NULL
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.info.observe(viewLifecycleOwner) {
            updatePreviewContentText()
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
        binding.editText.setOnClickListener {
            findNavController().navigate(R.id.action_input_content)
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

class ContentInputFragment : BaseFragment() {
    private val viewModel by activityViewModels<GeneratorViewModel>()
    private lateinit var binding: FragmentContentInputBinding
    override fun onCreateContent(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentInputBinding.inflate(inflater, container, false)
        viewModel.info.observe(viewLifecycleOwner) {
            binding.editText.setText(it.content)
        }
        return binding.root
    }

    override fun onSetMenuProvider(): MenuProvider = object : DoneMenuProvider() {
        override fun onDone(): Boolean {
            viewModel.info.value =
                viewModel.info.value!!.copy(content = binding.editText.text.toString())
            findNavController().navigateUp()
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        // show input method on resume
        binding.editText.apply {
            requestFocus()
            selectAll()
        }
        val imManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imManager.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onPause() {
        super.onPause()
        val imManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imManager.hideSoftInputFromWindow(binding.editText.windowToken, 0)
    }
}
