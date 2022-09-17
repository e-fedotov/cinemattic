package ru.evgenyfedotov.cinemattic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import ru.evgenyfedotov.cinemattic.workers.AlarmNotificationReceiver.Companion.CHANNEL_ID


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener (OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("firebase", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("firebase", token)
        })

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

        onNewIntent(intent)
        createNotificationChannel()

    }

    override fun onNewIntent(intent: Intent?) {
        val bundle = intent!!.extras
        if (bundle != null) {
            if (bundle.containsKey("detailsFragment")) {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.detailsFragment, bundle, null)
            }
        }
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notificationChannelName)
            val descriptionText = getString(R.string.notificationChannelDescription)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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