package ru.evgenyfedotov.cinemattic

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

class DetailsFragment : Fragment() {

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
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        arguments?.let { args ->
            poster.setImageDrawable(
                AppCompatResources.getDrawable(
                    view.context,
                    args.getInt(MainActivity.POSTER_KEY)
                )
            )

            Log.d("Test ---------->", args.toString())
//            activity?.title = view.context.getString(args.getInt(MainActivity.TITLE_KEY))
            toolbar.title = view.context.getString(args.getInt(MainActivity.TITLE_KEY))
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            title.text = view.context.getString(args.getInt(MainActivity.TITLE_KEY))
            year.text = view.context.getString(args.getInt(MainActivity.YEAR_KEY))
            description.text = view.context.getString(args.getInt(MainActivity.DESCRIPTION_KEY))
        }

        val data = Intent()

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

    }

    companion object {
        const val CHECKBOX_KEY = "Checkbox"
        const val DESCRIPTION_KEY = "Description"
    }

}