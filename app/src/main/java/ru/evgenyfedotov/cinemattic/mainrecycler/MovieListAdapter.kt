package ru.evgenyfedotov.cinemattic.mainrecycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.MainListFragment
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.model.MovieItem

class MovieListAdapter(private val items: MutableList<MovieItem>, private val favoriteMoviesList: LiveData<List<MovieItem>>, private val lifecycleOwner: LifecycleOwner, private val listener: MovieItemListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isFavorite: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MovieListViewHolder(inflater.inflate(R.layout.movie_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMovieId = items[position].filmId
        favoriteMoviesList.observe(lifecycleOwner) { favorites ->
            val findCurrentMovieInFavorites = favorites.find { item -> item.filmId == currentMovieId }
            isFavorite = findCurrentMovieInFavorites?.filmId == currentMovieId
        }
        when (holder) {
            is MovieListViewHolder -> holder.bind(items[position], isFavorite, listener)
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