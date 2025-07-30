package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : BasicActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var refreshButton: Button
    private lateinit var rvSongs: RecyclerView
    private lateinit var placeholderNoResults: LinearLayout
    private lateinit var placeholderError: LinearLayout

    private val trackList = ArrayList<Track>()

    private val trackAdapter = TrackAdapter(trackList)

    private var searchBarText: String = BASE_SEARCH_STRING

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

        inputEditText = findViewById(R.id.search_bar)
        clearButton = findViewById(R.id.clearIcon)
        toolbar = findViewById(R.id.toolbar)
        refreshButton = findViewById(R.id.refreshButton)
        rvSongs = findViewById(R.id.rvSongs)
        placeholderNoResults = findViewById(R.id.placeholderNoResults)
        placeholderError = findViewById(R.id.placeholderError)

        rvSongs.layoutManager = LinearLayoutManager(this)
        rvSongs.adapter = trackAdapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            showMessage("content")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            if (inputEditText.text.isNotEmpty()) {
                iTunesService.search(inputEditText.text.toString()).enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                        if (response.isSuccessful) {
                            val searchResponse = response.body()
                            if (searchResponse != null) {
                                trackList.clear()
                                if (searchResponse.results.isNotEmpty()) {
                                    trackList.addAll(searchResponse.results)
                                    trackAdapter.notifyDataSetChanged()
                                    showMessage("content")
                                } else {
                                    showMessage("no_results")
                                }
                            } else {
                                showMessage("error")
                            }
                        } else {
                            showMessage("error")
                        }
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        showMessage("error")
                    }
                })
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    iTunesService.search(inputEditText.text.toString()).enqueue(object : Callback<SearchResponse> {
                        override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                            if (response.isSuccessful) {
                                val searchResponse = response.body()
                                if (searchResponse != null) {
                                    trackList.clear()
                                    if (searchResponse.results.isNotEmpty()) {
                                        trackList.addAll(searchResponse.results)
                                        trackAdapter.notifyDataSetChanged()
                                        showMessage("content")
                                    } else {
                                        showMessage("no_results")
                                    }
                                } else {
                                    showMessage("error")
                                }
                            } else {
                                showMessage("error")
                            }
                        }

                        override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                            showMessage("error")
                        }
                    })
                }
                true
            } else {
                false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (s.isNullOrEmpty()) {
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    showMessage("content")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun showMessage(type: String) {
        when (type) {
            "content" -> {
                rvSongs.visibility = View.VISIBLE
                placeholderNoResults.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
            "no_results" -> {
                rvSongs.visibility = View.GONE
                placeholderNoResults.visibility = View.VISIBLE
                placeholderError.visibility = View.GONE
            }
            "error" -> {
                rvSongs.visibility = View.GONE
                placeholderNoResults.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_BAR_SAVED_TEXT, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchBarText = savedInstanceState.getString(SEARCH_BAR_SAVED_TEXT, BASE_SEARCH_STRING)
        inputEditText.setText(searchBarText)
    }
}