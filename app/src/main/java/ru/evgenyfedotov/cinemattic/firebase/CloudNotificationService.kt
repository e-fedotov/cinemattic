package ru.evgenyfedotov.cinemattic.firebase

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.internal.notify
import ru.evgenyfedotov.cinemattic.MainActivity
import ru.evgenyfedotov.cinemattic.R
import ru.evgenyfedotov.cinemattic.workers.AlarmNotificationReceiver

class CloudNotificationService : FirebaseMessagingService(){
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("Firebase", "onMessageReceived: Received")
        Log.d("Firebase", message.notification.toString())
        Log.d("Firebase", message.data.toString())
        notificationHandler(message)
    }

    private fun notificationHandler(message: RemoteMessage) {
        val movieId: String? = message.data["movieId"]
        val fragment: String? = message.data["detailsFragment"]
        val newIntent: Intent = Intent(this, MainActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        newIntent.putExtra("detailsFragment", fragment)
        newIntent.putExtra(MainActivity.MOVIE_ID, movieId)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            movieId
                ?.toInt()
                ?: AlarmNotificationReceiver.NOTIFICATION_ID,
            newIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this,
            AlarmNotificationReceiver.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        Log.d("notification", "onReceive: Here is notification")

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            movieId?.toInt() ?: AlarmNotificationReceiver.NOTIFICATION_ID, notification
        )
    }
}