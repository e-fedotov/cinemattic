package ru.evgenyfedotov.cinemattic.mainrecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListPagingAdapter.Companion.REPO_COMPARATOR
import ru.evgenyfedotov.cinemattic.model.MovieItem

class MovieListPagingAdapter(private val listener: MovieItemListener) :
    PagingDataAdapter<MovieItem, MovieListViewHolder>(REPO_COMPARATOR) {

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
        getItem(position)?.let { holder.bind(movie = it, listener) }
    }

}
