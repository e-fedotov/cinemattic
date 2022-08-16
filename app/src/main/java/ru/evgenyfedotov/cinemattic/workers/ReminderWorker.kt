package ru.evgenyfedotov.cinemattic.workers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.evgenyfedotov.cinemattic.MainActivity
import ru.evgenyfedotov.cinemattic.R

class ReminderWorker(private val context: Context, private val workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    override fun doWork(): Result {

        val title = inputData.getString(MainActivity.TITLE_KEY)
        val description = inputData.getString(MainActivity.DESCRIPTION_KEY)

        val intent: Intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("detailsFragment", "DetailsFragment")
        intent.putExtra(MainActivity.TITLE_KEY, title)
        intent.putExtra(MainActivity.DESCRIPTION_KEY, description)

        Log.d("work", "$title, $description")

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setContentTitle(title)
            .setContentText(description)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification.build())
        }


        Log.d("worker", "doWork: ")
        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "Reminder Notifications"
        const val NOTIFICATION_ID = 1
    }
}
