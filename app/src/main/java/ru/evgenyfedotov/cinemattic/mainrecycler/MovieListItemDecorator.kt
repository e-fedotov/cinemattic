package ru.evgenyfedotov.cinemattic.mainrecycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.R
import kotlin.math.roundToInt

class MovieListItemDecorator(context: Context) : RecyclerView.ItemDecoration() {

    private val dividerDrawable: Drawable? =
        AppCompatResources.getDrawable(context, R.drawable.custom_divider)
    private val bounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        super.onDraw(c, parent, state)
        c.save()
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)

            dividerDrawable?.let {
                val bottom: Int = bounds.bottom + child.translationY.roundToInt()
                val top: Int = bottom - dividerDrawable.intrinsicHeight
                val left = 0
                val right = parent.width
                bounds.set(left, top, right, bottom)
                it.draw(c)
            }
        }
        c.restore()
    }

}