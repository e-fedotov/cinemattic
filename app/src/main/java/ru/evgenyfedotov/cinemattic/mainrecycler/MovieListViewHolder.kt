package ru.evgenyfedotov.cinemattic.mainrecycler

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.DetailsActivity
import ru.evgenyfedotov.cinemattic.FavoritesActivity
import ru.evgenyfedotov.cinemattic.ui.FavoriteButton
import ru.evgenyfedotov.cinemattic.MainActivity
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.data.MovieItem

class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.title)
    private val year: TextView = itemView.findViewById(R.id.year)
    private val poster: ImageView = itemView.findViewById(R.id.poster)
    private val btn: Button = itemView.findViewById(R.id.detailsBtn)
    private val favBtn: FavoriteButton = itemView.findViewById(R.id.favBtn)

    fun bind(movie: MovieItem, listener: MovieItemListener) {
        title.text = itemView.context.getString(movie.titleId)
        year.text = itemView.context.getString(movie.yearId)
        poster.setImageDrawable(AppCompatResources.getDrawable(itemView.context, movie.posterId))

        if (FavoritesActivity.favorites.find { item -> movie.equals(item) } == movie) {
            favBtn.isChecked = true
        } else {
            favBtn.isChecked = false
        }
        favBtn.setOnClickListener {
            listener.onFavoriteClick(movie, favBtn.isChecked, adapterPosition)
        }

        btn.setOnClickListener {
            val intent = Intent(itemView.context, DetailsActivity::class.java)
            intent
                .putExtra(MainActivity.POSTER_KEY, movie.posterId)
                .putExtra(MainActivity.TITLE_KEY, movie.titleId)
                .putExtra(MainActivity.DESCRIPTION_KEY, movie.descriptionId)
                .putExtra(MainActivity.YEAR_KEY, movie.yearId)

            itemView.context.startActivity(intent)
        }


    }

}