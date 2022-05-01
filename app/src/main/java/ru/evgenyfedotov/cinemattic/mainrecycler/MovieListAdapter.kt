package ru.evgenyfedotov.cinemattic.mainrecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.data.MovieItem

class MovieListAdapter(private val items: List<MovieItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MovieListViewHolder(inflater.inflate(R.layout.movie_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieListViewHolder -> holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size
}