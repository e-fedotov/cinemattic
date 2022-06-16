package ru.evgenyfedotov.cinemattic

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import ru.evgenyfedotov.cinemattic.data.MovieItem
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieItemListener
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListAdapter
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListItemDecorator

class FavoritesActivity : AppCompatActivity() {

    val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(this, 2)
            else -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MovieListAdapter(favorites, object : MovieItemListener {
            override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
                    favorites.remove(item)
                    recyclerView.adapter?.notifyItemRemoved(position)

            }
        })

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) recyclerView.addItemDecoration(
            MovieListItemDecorator(this)
        )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        val favorites = mutableListOf<MovieItem>()
    }
}