package ru.evgenyfedotov.cinemattic

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import ru.evgenyfedotov.cinemattic.data.MovieItem
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieItemListener
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListAdapter
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListItemDecorator

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(view.context, 2)
            else -> LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MovieListAdapter(favorites, object : MovieItemListener {
            override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
                favorites.remove(item)
                recyclerView.adapter?.notifyItemRemoved(position)

            }
        })
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) recyclerView.addItemDecoration(
            MovieListItemDecorator(view.context)
        )

    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_activity_favorites)
    }
    companion object {
        val favorites = mutableListOf<MovieItem>()
    }
}