package ru.evgenyfedotov.cinemattic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val poster: ImageView = findViewById(R.id.detailsPoster)
        val title: TextView = findViewById(R.id.detailsTitle)
        val year: TextView = findViewById(R.id.detailsYear)
        val description: TextView = findViewById(R.id.detailsDescription)

        val checkbox: CheckBox = findViewById(R.id.checkBox)
        val commentaryField: TextInputEditText = findViewById(R.id.commentary)
        val btnBack: Button = findViewById(R.id.btnBack)
        val btnShare: Button = findViewById(R.id.btnShare)


        poster.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                intent.getIntExtra(MainActivity.POSTER_KEY, -1)
            )
        )

        this.title = getString(intent.getIntExtra(MainActivity.TITLE_KEY, -1))
        title.text = getString(intent.getIntExtra(MainActivity.TITLE_KEY, -1))
        year.text = getString(intent.getIntExtra(MainActivity.YEAR_KEY, -1))
        description.text = getString(intent.getIntExtra(MainActivity.DESCRIPTION_KEY, -1))

        val data = Intent()

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, commentaryField.text.toString())
            shareIntent.type = "text/plain"

            val shareIntentChooser = Intent.createChooser(shareIntent, "Share your thoughts on ${title.text}")
            startActivity(shareIntentChooser)
        }

//        checkbox.setOnCheckedChangeListener { checkbox, value ->
//            data.putExtra(CHECKBOX_KEY, value)
//        }
//
//        commentaryField.addTextChangedListener(afterTextChanged =
//        {
//            data.putExtra(DESCRIPTION_KEY, it.toString())
//        })

        btnBack.setOnClickListener {
            data.putExtra(CHECKBOX_KEY, checkbox.isChecked)

            commentaryField.text?.let {
                data.putExtra(
                    DESCRIPTION_KEY,
                    commentaryField.text.toString()
                )
            }

            setResult(RESULT_OK, data)
            finish()
        }

    }

    companion object {
        const val CHECKBOX_KEY = "Checkbox"
        const val DESCRIPTION_KEY = "Description"
    }

}