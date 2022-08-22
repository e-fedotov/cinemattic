package ru.evgenyfedotov.cinemattic

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.format.DateUtils
import android.text.style.TtsSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.impl.background.systemalarm.SystemAlarmService
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import ru.evgenyfedotov.cinemattic.di.DaggerDetailsFragmentComponent
import ru.evgenyfedotov.cinemattic.di.DaggerFavoritesFragmentComponent
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.viewmodel.DetailsViewModel
import ru.evgenyfedotov.cinemattic.viewmodel.DetailsViewModelFactory
import ru.evgenyfedotov.cinemattic.workers.AlarmNotificationReceiver
import ru.evgenyfedotov.cinemattic.workers.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailsFragment : Fragment() {

    @Inject
    lateinit var detailsViewModelFactory: DetailsViewModelFactory
    private val viewModel: DetailsViewModel by viewModels { detailsViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)

        DaggerDetailsFragmentComponent.builder()
            .applicationComponent(App.getAppInstance())
            .build()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val poster: ImageView = view.findViewById(R.id.detailsPoster)

        val title: TextView = view.findViewById(R.id.detailsTitle)
        val year: TextView = view.findViewById(R.id.detailsYear)
        val description: TextView = view.findViewById(R.id.detailsDescription)

        val checkbox: CheckBox = view.findViewById(R.id.checkBox)
        val commentaryField: TextInputEditText = view.findViewById(R.id.commentary)
        val btnBack: Button = view.findViewById(R.id.btnBack)
        val btnShare: Button = view.findViewById(R.id.btnShare)
        val btnSchedule: Button = view.findViewById(R.id.btnSchedule)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)


        val movieId = arguments?.getString(MainActivity.MOVIE_ID)?.toInt()
        movieId?.let { id ->
            viewModel.getMovieById(id)
        }

        var movieTitle: String? = ""
        var movieDescription: String? = ""

        postponeEnterTransition()

        viewModel.movieItem.observe(viewLifecycleOwner) { movie ->
            Glide.with(view)
                .load(movie?.posterUrl)
                .into(poster)

            poster.transitionName = movieId.toString()
            title.text = movie?.nameEn ?: movie?.nameRu
            year.text = movie?.year
            description.text = movie?.description

            movieTitle = movie?.nameEn ?: movie?.nameRu
            movieDescription = movie.description

            toolbar.title = movie?.nameEn ?: movie?.nameRu
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            toolbar.navigationIcon?.colorFilter =
                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }

//        arguments?.let { args ->
//
//            toolbar.title = args.getString(MainActivity.TITLE_KEY)
//
//            title.text = args.getString(MainActivity.TITLE_KEY)
//            year.text = args.getString(MainActivity.YEAR_KEY)
//            description.text = args.getString(MainActivity.DESCRIPTION_KEY)
//        }

        val data = Intent()

        val reminderDate = Calendar.getInstance()

        // Time picker
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("Set time for reminder")
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setTimeFormat(CLOCK_24H)
            .build()

        // Date dialog builder
        val datePickerDialog =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Remind me later")
                .build()

        datePickerDialog.addOnPositiveButtonClickListener { date ->

            val computedDelay = date - MaterialDatePicker.todayInUtcMilliseconds()
            val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(computedDelay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        MainActivity.TITLE_KEY       to title.text.toString(),
                        MainActivity.DESCRIPTION_KEY to description.text.toString(),
                        MainActivity.MOVIE_ID        to movieId.toString()
                    )
                )
                .build()

//            WorkManager
//                .getInstance(requireContext())
//                .enqueue(reminderWorkRequest)

            timePicker.show(parentFragmentManager, "Time")
            timePicker.addOnPositiveButtonClickListener {
                reminderDate.timeInMillis = date
                reminderDate.set(Calendar.HOUR, timePicker.hour)
                reminderDate.set(Calendar.MINUTE, timePicker.minute)

                scheduleAlarm(requireContext(), reminderDate, movieTitle, movieDescription, movieId.toString())

                Log.d("calendar", "${reminderDate.toString()} ")
            }
        }


        /*
         *   Button listeners
         */

        // Share Button
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, commentaryField.text.toString())
            shareIntent.type = "text/plain"

            val shareIntentChooser =
                Intent.createChooser(shareIntent, "Share your thoughts on ${title.text}")
            startActivity(shareIntentChooser)
        }

        // Back Button
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Schedule a reminder Button
        btnSchedule.setOnClickListener {
            datePickerDialog.show(parentFragmentManager, "Date")
        }

        startPostponedEnterTransition()

    }

    private fun scheduleAlarm(context: Context, date: Calendar, movieTitle: String?, movieDescription: String?, movieId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java)

        alarmIntent.putExtra(MainActivity.MOVIE_ID, movieId)
        alarmIntent.putExtra(MainActivity.TITLE_KEY, movieTitle)
        alarmIntent.putExtra(MainActivity.DESCRIPTION_KEY, movieDescription)

        val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, movieId.toInt(), alarmIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT )

        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.timeInMillis, alarmPendingIntent)

    }

    companion object {
        const val CHECKBOX_KEY = "Checkbox"
        const val DESCRIPTION_KEY = "Description"
    }

}