package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    private var searchBarText: String = BASE_SEARCH_STRING
    private lateinit var inputEditText: EditText

    companion object {
        const val SEARCH_BAR_SAVED_TEXT = "PRODUCT_AMOUNT"
        const val BASE_SEARCH_STRING = ""
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val backFromSettingsButton: Button = findViewById(R.id.backFromSettingsButton) // add <>
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        inputEditText = findViewById<EditText>(R.id.search_bar)


        clearButton.setOnClickListener {
            inputEditText.setText("")
        }

        backFromSettingsButton.setOnClickListener {
            val  backFromSettingsActivity = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(backFromSettingsActivity)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {

                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        val inputEditText = findViewById<EditText>(R.id.search_bar)
        outState.putString(SEARCH_BAR_SAVED_TEXT, inputEditText.text.toString())
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchBarText = savedInstanceState.getString(SEARCH_BAR_SAVED_TEXT, BASE_SEARCH_STRING)

        val inputEditText = findViewById<EditText>(R.id.search_bar)
        inputEditText.setText(searchBarText)
    }
}