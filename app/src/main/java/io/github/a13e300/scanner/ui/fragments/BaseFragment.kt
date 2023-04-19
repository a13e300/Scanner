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
        // 填充 fragment_base ，确保每个 fragment 都有 CoordinatorLayout
        val view = inflater.inflate(R.layout.fragment_base, container, false)
        val newContainer = view.findViewById<ViewGroup>(R.id.license_detail)
        // 实际的 Fragment view 在这里填充并加入
        val content = onCreateContent(inflater, newContainer, savedInstanceState)
        newContainer.addView(content)
        return view
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetMenuProvider()?.let {
            // 由于 Fragments 共用 MainActivity 的 Toolbar 菜单
            // 需要使用 viewLifecycleOwner 确保 Fragment 离开时菜单被正确移除，仅当 resume 的时候显示
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
        // 显示 SnackBar 提示
        view?.let {
            Snackbar.make(
                it,
                msg,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}