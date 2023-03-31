package io.github.a13e300.scanner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import io.github.a13e300.scanner.R

abstract class BaseFragment : Fragment() {
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_base, container, false)
        val newContainer = view.findViewById<ViewGroup>(R.id.content)
        val content = onCreateContent(inflater, newContainer, savedInstanceState)
        newContainer.addView(content)
        return view
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetMenuProvider()?.let {
            requireActivity().addMenuProvider(it, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    protected open fun onSetMenuProvider(): MenuProvider? = null

    abstract fun onCreateContent(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    protected fun showSnackBar(msg: String) {
        view?.let {
            Snackbar.make(
                it,
                msg,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}