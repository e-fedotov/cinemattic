package ru.evgenyfedotov.cinemattic

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.data.MovieItem
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListAdapter

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }

    private val movieItems = mutableListOf<MovieItem>(
        MovieItem(titleId = R.string.black_panther_title, posterId = R.drawable.poster_bpanther, yearId = R.string.black_panther_year, descriptionId = R.string.black_panther_details),
        MovieItem(titleId = R.string.goonies_title, posterId = R.drawable.poster_goonies, yearId = R.string.goonies_year, descriptionId = R.string.goonies_description),
        MovieItem(titleId = R.string.starwars7_title, posterId = R.drawable.poster_starwars7, yearId = R.string.starwars7_year, descriptionId = R.string.starwars7_details),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(this, 2)
            else -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MovieListAdapter(movieItems)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        const val INTENT_RESULT_TAG = "IntentResults"
        const val POSTER_KEY = "poster"
        const val TITLE_KEY = "title"
        const val YEAR_KEY = "year"
        const val DESCRIPTION_KEY = "description"

    }

}