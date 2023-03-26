package io.github.a13e300.scanner.ui.generator

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.StorageUtils
import io.github.a13e300.scanner.databinding.FragmentGeneratorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<GeneratorViewModel>()

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
                Snackbar.make(
                    binding.root,
                    "saved",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.image.observe(viewLifecycleOwner) {
            binding.image.setImageBitmap(it)
        }
        binding.editText.setText(viewModel.content.value)
        binding.button.setOnClickListener {
            viewModel.content.value = binding.editText.text.toString()
            viewModel.updateQRCode()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}