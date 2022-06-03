package ru.evgenyfedotov.cinemattic.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.Checkable


class FavoriteButton(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs), Checkable {

    private var mChecked: Boolean = false
    private val stateCheckedState = intArrayOf(android.R.attr.state_checked)

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) mergeDrawableStates(drawableState, stateCheckedState)
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val onClickListener: View.OnClickListener = OnClickListener() { view ->
            toggle()
            l?.onClick(view)
        }
        println("$isChecked")
        super.setOnClickListener(onClickListener)
    }
}