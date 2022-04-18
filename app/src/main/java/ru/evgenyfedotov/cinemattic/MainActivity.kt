package ru.evgenyfedotov.cinemattic

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var title1: TextView
    private lateinit var title2: TextView
    private lateinit var title3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title1 = findViewById(R.id.title1)
        title2 = findViewById(R.id.title2)
        title3 = findViewById(R.id.title3)

        val btn1: Button = findViewById(R.id.detailsBtn1)
        val btn2: Button = findViewById(R.id.detailsBtn2)
        val btn3: Button = findViewById(R.id.detailsBtn3)

        val intent = Intent(this, DetailsActivity::class.java)

        val startDetailsActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result->
            val data = result.data
            if (result.resultCode == RESULT_OK && data != null) {
                Log.d(INTENT_RESULT_TAG, data.getBooleanExtra(DetailsActivity.CHECKBOX_KEY, false).toString())
                data.getStringExtra(DetailsActivity.DESCRIPTION_KEY)?.let { Log.d(INTENT_RESULT_TAG, it) }
            }
        }

        btn1.setOnClickListener {
            title1.setTextColor(ContextCompat.getColor(this, R.color.teal_200))

            intent
                .putExtra(POSTER_KEY, R.drawable.poster_bpanther)
                .putExtra(TITLE_KEY, R.string.black_panther_title)
                .putExtra(YEAR_KEY, R.string.black_panther_year)
                .putExtra(DESCRIPTION_KEY, R.string.black_panther_details)

            startDetailsActivity.launch(intent)
        }

        btn2.setOnClickListener {
            title2.setTextColor(ContextCompat.getColor(this, R.color.teal_200))

            intent
                .putExtra(POSTER_KEY, R.drawable.poster_goonies)
                .putExtra(TITLE_KEY, R.string.goonies_title)
                .putExtra(YEAR_KEY, R.string.goonies_year)
                .putExtra(DESCRIPTION_KEY, R.string.goonies_description)

            startDetailsActivity.launch(intent)
        }

        btn3.setOnClickListener {
            title3.setTextColor(ContextCompat.getColor(this, R.color.teal_200))

            intent
                .putExtra(POSTER_KEY, R.drawable.poster_starwars7)
                .putExtra(TITLE_KEY, R.string.starwars7_title)
                .putExtra(YEAR_KEY, R.string.starwars7_year)
                .putExtra(DESCRIPTION_KEY, R.string.starwars7_details)

            startDetailsActivity.launch(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("title1", title1.currentTextColor)
        outState.putInt("title2", title2.currentTextColor)
        outState.putInt("title3", title3.currentTextColor)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        title1.setTextColor(savedInstanceState.getInt("title1"))
        title2.setTextColor(savedInstanceState.getInt("title2"))
        title3.setTextColor(savedInstanceState.getInt("title3"))
    }

    companion object {
        const val INTENT_RESULT_TAG = "IntentResults"
        const val POSTER_KEY = "poster"
        const val TITLE_KEY = "title"
        const val YEAR_KEY = "year"
        const val DESCRIPTION_KEY = "description"

    }

}