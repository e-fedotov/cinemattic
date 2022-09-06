package ru.evgenyfedotov.cinemattic.mainrecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListPagingAdapter.Companion.REPO_COMPARATOR
import ru.evgenyfedotov.cinemattic.model.MovieItem

class MovieListPagingAdapter(private val lifecycleOwner: LifecycleOwner, private val favoriteMoviesList: LiveData<List<MovieItem>>, private val listener: MovieItemListener) :
    PagingDataAdapter<MovieItem, MovieListViewHolder>(REPO_COMPARATOR) {

    private var isFavorite = false

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean =
                oldItem.filmId == newItem.filmId

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean =
                oldItem.filmId == newItem.filmId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MovieListViewHolder(inflater.inflate(R.layout.movie_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        val currentMovieId = getItem(position)?.filmId
        favoriteMoviesList.observe(lifecycleOwner) { favorites ->
            val findCurrentMovieInFavorites = favorites.find { item -> item.filmId == currentMovieId }
            isFavorite = findCurrentMovieInFavorites?.filmId == currentMovieId
        }
        getItem(position)?.let {
            holder.bind(movie = it, isFavorite, listener)
        }
    }

}
