package ru.evgenyfedotov.cinemattic.workers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import androidx.core.app.NotificationCompat
import ru.evgenyfedotov.cinemattic.MainActivity
import ru.evgenyfedotov.cinemattic.R

class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val newIntent: Intent = Intent(context, MainActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        newIntent.putExtra("detailsFragment", "DetailsFragment")
//        intent.putExtra(MainActivity.TITLE_KEY, title)
//        intent.putExtra(MainActivity.DESCRIPTION_KEY, description)
        newIntent.putExtra(MainActivity.MOVIE_ID, intent?.getStringExtra(MainActivity.MOVIE_ID))

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            intent?.getStringExtra(MainActivity.MOVIE_ID)
                ?.toInt()
                ?: NOTIFICATION_ID,
            newIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setContentTitle(intent?.getStringExtra(MainActivity.TITLE_KEY))
            .setContentText(intent?.getStringExtra(MainActivity.DESCRIPTION_KEY))
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        Log.d("notification", "onReceive: Here is notification")

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            intent?.getStringExtra(MainActivity.MOVIE_ID)?.toInt() ?: NOTIFICATION_ID, notification
        )
    }

    companion object {
        const val CHANNEL_ID      = "Reminder Notifications"
        const val NOTIFICATION_ID = 1
    }
}