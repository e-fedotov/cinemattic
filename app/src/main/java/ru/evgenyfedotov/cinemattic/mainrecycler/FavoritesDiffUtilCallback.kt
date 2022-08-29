package ru.evgenyfedotov.cinemattic.mainrecycler

import androidx.recyclerview.widget.DiffUtil
import ru.evgenyfedotov.cinemattic.model.MovieItem

class FavoritesDiffUtilCallback(private val oldList: List<MovieItem>, private val newList: List<MovieItem>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].filmId == newList[newItemPosition].filmId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].filmId == newList[newItemPosition].filmId -> true
            else -> false
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}