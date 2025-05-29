package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager

import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : BasicActivity() {
    private var searchBarText: String = BASE_SEARCH_STRING
    private lateinit var inputEditText: EditText

    companion object {
        const val SEARCH_BAR_SAVED_TEXT = "PRODUCT_AMOUNT"
        const val BASE_SEARCH_STRING = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val rootLinearLayout = findViewById<LinearLayout>(R.id.rootView)
        setupEdgeToEdge(rootLinearLayout)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        inputEditText = findViewById<EditText>(R.id.search_bar)


        clearButton.setOnClickListener {
            inputEditText.setText("")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_BAR_SAVED_TEXT, inputEditText.text.toString())
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchBarText = savedInstanceState.getString(SEARCH_BAR_SAVED_TEXT, BASE_SEARCH_STRING)

        val inputEditText = findViewById<EditText>(R.id.search_bar)
        inputEditText.setText(searchBarText)
    }
}