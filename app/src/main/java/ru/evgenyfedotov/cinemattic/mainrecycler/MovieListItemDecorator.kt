package ru.evgenyfedotov.cinemattic.mainrecycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.R
import kotlin.math.roundToInt

class MovieListItemDecorator(context: Context) : RecyclerView.ItemDecoration() {

    private val dividerDrawable: Drawable? =
        AppCompatResources.getDrawable(context, R.drawable.custom_divider)
    private val bounds = Rect()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        c.save()
        val childCount = parent.childCount

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)

            dividerDrawable?.let {
                val bottom: Int = bounds.bottom + child.marginBottom
                val top: Int = bounds.bottom - child.marginBottom
                val left = child.marginStart
                val right = child.width + child.marginEnd
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
        c.restore()
        super.onDraw(c, parent, state)
    }

}