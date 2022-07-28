package ru.evgenyfedotov.cinemattic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)

        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomMainBtn -> {
                    navController.navigate(R.id.mainListFragment)
                    true
                }
                R.id.favoritesMenuBtn -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }
                else -> false
            }
        }

//        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.title = "SD"

    }

    override fun onResume() {
        recyclerView.adapter?.notifyDataSetChanged()
        super.onResume()
    }

    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is DetailsFragment) {
                    supportFragmentManager.popBackStack()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Are you sure?")
                        .setMessage("Are you sure you want to quit this app?")
                        .setPositiveButton("Confirm") { dialog, which ->
                            super.finish()
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        }

    }

    companion object {
        const val INTENT_RESULT_TAG = "IntentResults"
        const val POSTER_KEY = "poster"
        const val TITLE_KEY = "title"
        const val YEAR_KEY = "year"
        const val DESCRIPTION_KEY = "description"
        const val MOVIE_ID = "movieId"
    }

}