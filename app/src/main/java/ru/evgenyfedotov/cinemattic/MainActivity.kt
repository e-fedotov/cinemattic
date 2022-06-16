package ru.evgenyfedotov.cinemattic

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.evgenyfedotov.cinemattic.data.MovieItem
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieItemListener
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListAdapter
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListItemDecorator

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }

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

    private val recyclerAdapter = MovieListAdapter(movieItems, object : MovieItemListener {
        override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
            if (isFavorite) {
                FavoritesActivity.favorites.add(item)
            } else {
                FavoritesActivity.favorites.remove(item)
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(this, 2)
            else -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.favoritesMenuBtn -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }

                else -> false
            }
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) recyclerView.addItemDecoration(
            MovieListItemDecorator(this)
        )

    }

    override fun onResume() {
        recyclerView.adapter?.notifyDataSetChanged()
        super.onResume()
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Are you sure?")
            .setMessage("Are you sure you want to quit this app?")
            .setPositiveButton("Confirm") { dialog, which ->
                super.onBackPressed()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        const val INTENT_RESULT_TAG = "IntentResults"
        const val POSTER_KEY = "poster"
        const val TITLE_KEY = "title"
        const val YEAR_KEY = "year"
        const val DESCRIPTION_KEY = "description"
    }

}