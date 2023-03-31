package io.github.a13e300.scanner.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.utils.widget.ImageFilterButton

class ImageActionButton(context: Context, attributeSet: AttributeSet) : ImageFilterButton(context, attributeSet) {
    init {
        contentDescription?.let {
            TooltipCompat.setTooltipText(this, it)
        }
    }
}