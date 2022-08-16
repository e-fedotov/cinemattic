package ru.evgenyfedotov.cinemattic

import android.app.DatePickerDialog
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
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import ru.evgenyfedotov.cinemattic.workers.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

class DetailsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
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


        postponeEnterTransition()
        arguments?.let { args ->
            Glide.with(view)
                .load(args.getString(MainActivity.POSTER_KEY))
                .into(poster)

            poster.transitionName = args.getString(MainActivity.MOVIE_ID)

            toolbar.title = args.getString(MainActivity.TITLE_KEY)
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            toolbar.navigationIcon?.colorFilter =
                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            title.text = args.getString(MainActivity.TITLE_KEY)
            year.text = args.getString(MainActivity.YEAR_KEY)
            description.text = args.getString(MainActivity.DESCRIPTION_KEY)
        }

        val data = Intent()

        // Date dialog builder
        val datePickerDialog =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Remind me later")
                .build()



        datePickerDialog.addOnPositiveButtonClickListener { date ->

            val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
//                .setInitialDelay(date, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    MainActivity.TITLE_KEY to title.text.toString(),
                    MainActivity.DESCRIPTION_KEY to description.text.toString()
                ))
                .build()

            WorkManager
                .getInstance(requireContext())
                .enqueue(reminderWorkRequest)
//            Log.d("Date", "onViewCreated: $date, ${MaterialDatePicker.todayInUtcMilliseconds()}")
//            Log.d("Date", DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE).toString())
        }

        // Button listeners
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, commentaryField.text.toString())
            shareIntent.type = "text/plain"

            val shareIntentChooser =
                Intent.createChooser(shareIntent, "Share your thoughts on ${title.text}")
            startActivity(shareIntentChooser)
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

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