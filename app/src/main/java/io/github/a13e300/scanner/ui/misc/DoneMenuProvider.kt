package io.github.a13e300.scanner.ui.misc

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import io.github.a13e300.scanner.R

open class DoneMenuProvider : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        return menuInflater.inflate(R.menu.done_button_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.done -> onDone()
            else -> false
        }

    protected open fun onDone(): Boolean {
        return false
    }
}
