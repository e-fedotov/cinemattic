package ru.evgenyfedotov.cinemattic

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import ru.evgenyfedotov.cinemattic.data.MovieItem
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieItemListener
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListAdapter
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListItemDecorator

class MainListFragment : Fragment() {

    private val movieItems = mutableListOf<MovieItem>(
        MovieItem(
            titleId = R.string.black_panther_title,
            posterId = R.drawable.poster_bpanther,
            yearId = R.string.black_panther_year,
            descriptionId = R.string.black_panther_details
        ),
        MovieItem(
            titleId = R.string.goonies_title,
            posterId = R.drawable.poster_goonies,
            yearId = R.string.goonies_year,
            descriptionId = R.string.goonies_description
        ),
        MovieItem(
            titleId = R.string.starwars7_title,
            posterId = R.drawable.poster_starwars7,
            yearId = R.string.starwars7_year,
            descriptionId = R.string.starwars7_details
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val recyclerAdapter = MovieListAdapter(movieItems, object : MovieItemListener {
            override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
                if (isFavorite) {

                    FavoritesFragment.favorites.add(item)

                    Snackbar.make(view.findViewById(R.id.constraintLayout), getString(R.string.snackbar_favorites_added), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo)) {
                            FavoritesFragment.favorites.remove(item)
                            recyclerView.adapter?.notifyItemChanged(position)
                        }
                        .show()

                } else {
                    FavoritesFragment.favorites.remove(item)
                    Snackbar.make(view.findViewById(R.id.constraintLayout), getString(R.string.snackbar_favorites_removed), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo)) {
                            FavoritesFragment.favorites.add(item)
                            recyclerView.adapter?.notifyItemChanged(position)
                        }
                        .show()
                }
            }
        })

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(view.context, 2)
            else -> LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = layoutManager

    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
    }

}