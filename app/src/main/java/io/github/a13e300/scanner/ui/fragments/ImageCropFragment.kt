package io.github.a13e300.scanner.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import io.github.a13e300.scanner.R
import io.github.a13e300.scanner.databinding.FragmentImageCropBinding
import io.github.a13e300.scanner.parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ImageCropRequest(
    val uri: Uri,
    val request: String,
    val size: Int,
    val dest: Uri?
): Parcelable

class ImageCropFragment : Fragment() {

    private lateinit var mRequest: ImageCropRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mRequest = it.parcelable(ARG_URI)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageCropBinding.inflate(inflater, container, false)
        binding.cropImageView.apply {
            setImageUriAsync(mRequest.uri)
            setAspectRatio(1, 1)
            setOnCropImageCompleteListener { _, result ->
                setFragmentResult(mRequest.request, bundleOf(ARG_RESULT to result.uriContent))
                findNavController().navigateUp()
            }
        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                return menuInflater.inflate(R.menu.done_button_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.done -> {
                        binding.cropImageView.croppedImageAsync(
                            reqWidth = mRequest.size,
                            reqHeight = mRequest.size,
                            customOutputUri = mRequest.dest
                        )
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner)
        return binding.root
    }

    companion object {
        private const val ARG_URI = "uri"
        const val ARG_RESULT = "result"

        @JvmStatic
        fun newInstance(param1: Uri?) =
            ImageCropFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, param1)
                }
            }
    }
}