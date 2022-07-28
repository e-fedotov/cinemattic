package ru.evgenyfedotov.cinemattic.mainrecycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.model.MovieItem

class MovieListAdapter(private val items: MutableList<MovieItem>, private val listener: MovieItemListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MovieListViewHolder(inflater.inflate(R.layout.movie_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieListViewHolder -> holder.bind(items[position], listener)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<MovieItem>) {
        val diffResult = DiffUtil.calculateDiff(FavoritesDiffUtilCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

}

interface MovieItemListener {
    fun onFavoriteClick (item: MovieItem, isFavorite: Boolean, position: Int)
}