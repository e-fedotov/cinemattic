package ru.evgenyfedotov.cinemattic

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import ru.evgenyfedotov.cinemattic.di.DaggerDetailsFragmentComponent
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModel
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModelFactory
import ru.evgenyfedotov.cinemattic.workers.AlarmNotificationReceiver
import java.util.*
import javax.inject.Inject

class DetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

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
            if (movie == null) { // Hide all buttons if we didn't get movie info
                commentaryField.isVisible = false
                btnShare.isVisible        = false
                btnSchedule.isVisible     = false
                checkbox.isVisible        = false
            }
            Glide.with(view)
                .load(movie?.posterUrl)
                .into(poster)

            checkbox.isChecked = viewModel.isMovieFavorite(movie)

            poster.transitionName = movieId.toString()
            title.text = movie?.nameEn ?: movie?.nameRu
            year.text = movie?.year
            description.text = movie?.description
                ?: getString(R.string.movieDescriptionError)

            movieTitle = movie?.nameEn ?: movie?.nameRu
            movieDescription = movie?.description

            // Toolbar setup
            toolbar.title = movie?.nameEn ?: movie?.nameRu
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            toolbar.navigationIcon?.colorFilter =
                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            // Favorites Button
            checkbox.setOnCheckedChangeListener { btn, state ->
                when(state) {
                    true  -> viewModel.addToFavorites(movie)
                    false -> viewModel.removeFromFavorites(movie)
                }
            }
        }

        val reminderDate = Calendar.getInstance()

        // Time picker
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(getString(R.string.timePickerTitle))
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setTimeFormat(CLOCK_24H)
            .build()

        // Date dialog builder
        val datePickerDialog =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.dateDialogTitle))
                .build()

        datePickerDialog.addOnPositiveButtonClickListener { date ->

            timePicker.show(parentFragmentManager, "Time")
            timePicker.addOnPositiveButtonClickListener {
                reminderDate.timeInMillis = date
                reminderDate.set(Calendar.HOUR, timePicker.hour)
                reminderDate.set(Calendar.MINUTE, timePicker.minute)

                viewModel.scheduleAlarm(
                    requireContext(),
                    reminderDate,
                    movieTitle,
                    movieDescription,
                    movieId.toString()
                )

                Log.d("calendar", "${reminderDate.toString()} ")
            }
        }


        //
        // Button listeners
        //

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

    companion object {
        const val CHECKBOX_KEY = "Checkbox"
        const val DESCRIPTION_KEY = "Description"
    }

}