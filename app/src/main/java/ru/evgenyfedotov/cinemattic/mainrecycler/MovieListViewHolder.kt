package ru.evgenyfedotov.cinemattic.mainrecycler

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.evgenyfedotov.cinemattic.FavoritesFragment
import ru.evgenyfedotov.cinemattic.ui.FavoriteButton
import ru.evgenyfedotov.cinemattic.MainActivity
import ru.evgenyfedotov.cinemattic.MainListFragment
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.model.MovieItem

class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.title)
    private val year: TextView = itemView.findViewById(R.id.year)
    private val poster: ImageView = itemView.findViewById(R.id.poster)
    private val btn: Button = itemView.findViewById(R.id.detailsBtn)
    private val favBtn: FavoriteButton = itemView.findViewById(R.id.favBtn)

    fun bind(movie: MovieItem, isFavorite: Boolean, listener: MovieItemListener) {
        title.text = movie.nameEn ?: movie.nameRu
        year.text = movie.year
        Glide.with(itemView)
            .load(movie.posterUrlPreview)
            .into(poster)

//        favBtn.isChecked = isFavorite
        favBtn.isChecked = movie.isFavorite
        favBtn.setOnClickListener {
            listener.onFavoriteClick(movie, !favBtn.isChecked, bindingAdapterPosition)
        }

        btn.setOnClickListener { view ->

            val args = Bundle()
            val movieName = movie.nameEn ?: movie.nameRu

            args.putString(MainActivity.MOVIE_ID, movie.filmId.toString())
            args.putString(MainActivity.POSTER_KEY, movie.posterUrl)
            args.putString(MainActivity.TITLE_KEY, movieName)
            args.putString(MainActivity.DESCRIPTION_KEY, movie.description)
            args.putString(MainActivity.YEAR_KEY, movie.year)

            poster.apply {
                transitionName = movie.filmId.toString()
            }

            val extras = FragmentNavigatorExtras(
                poster to movie.filmId.toString()
            )

            view.findNavController()
                .navigate(R.id.detailsFragment, args, null, extras)
        }


    }

}