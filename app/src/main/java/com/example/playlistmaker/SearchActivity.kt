package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var rvSearchHistory: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    
    private val trackList = ArrayList<Track>()
    private val historyTrackList = ArrayList<Track>()

    private var searchBarText: String = BASE_SEARCH_STRING

    companion object {
        const val SEARCH_BAR_SAVED_TEXT = "PRODUCT_AMOUNT"
        const val BASE_SEARCH_STRING = ""

        const val SEARCH_HISTORY_REFERENCES = "search_history_references"
        const val MESSAGE_CONTENT = "content"
        const val MESSAGE_NO_RESULTS = "no_results"
        const val MESSAGE_ERROR = "error"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val sharedPrefs = getSharedPreferences(SEARCH_HISTORY_REFERENCES, MODE_PRIVATE)

        val rootLinearLayout = findViewById<LinearLayout>(R.id.rootView)
        setupEdgeToEdge(rootLinearLayout)

        inputEditText = findViewById(R.id.search_bar)
        clearButton = findViewById(R.id.clearIcon)
        toolbar = findViewById(R.id.toolbar)
        refreshButton = findViewById(R.id.refreshButton)
        rvSongs = findViewById(R.id.rvSongs)
        placeholderNoResults = findViewById(R.id.placeholderNoResults)
        placeholderError = findViewById(R.id.placeholderError)
        searchHistoryContainer = findViewById(R.id.searchHistoryContainer)
        rvSearchHistory = findViewById(R.id.rvSearchHistory)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        searchHistory = SearchHistory(sharedPrefs)
        
        // нужно обновлять адаптеры после тычка на песню из найднных, а также на песню из истории поиска
        // чтобы избежать дублирования кода, мы будем передавать updateSearchHistory() внутрь
        trackAdapter = TrackAdapter(trackList) { track ->
            searchHistory.saveTrack(track)
            updateSearchHistory()
        }
        historyAdapter = TrackAdapter(historyTrackList) { track ->
            searchHistory.saveTrack(track)
            updateSearchHistory()
        }

        rvSongs.layoutManager = LinearLayoutManager(this)
        rvSongs.adapter = trackAdapter
        
        rvSearchHistory.layoutManager = LinearLayoutManager(this)
        rvSearchHistory.adapter = historyAdapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            updateSearchHistory()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
        
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory() // удаляем содержимое
            historyTrackList.clear() // не забываем перерисовать!!
            historyAdapter.notifyDataSetChanged()
            updateSearchHistory()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            if (inputEditText.text.isNotEmpty()) {
                performSearch(inputEditText.text.toString())
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    performSearch(inputEditText.text.toString())
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
                }
                
                // показываем историю только если поле в фокусе и пустое
                searchHistoryContainer.visibility = if (inputEditText.hasFocus() && s.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryContainer.visibility = if (hasFocus && inputEditText.text.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // Инициализация истории при запуске
        updateSearchHistory()
    }
    // Приемлемо ли?
    private fun updateSearchHistory() { // Нужно обновить: Получили -> Очистили список на который смотрел адаптер -> Добавили все элементы из истории -> Попросили перерисовать
        val history = searchHistory.getSearchHistory()
        historyTrackList.clear()
        historyTrackList.addAll(history)
        historyAdapter.notifyDataSetChanged()
    }

    private fun showMessage(type: String) {
        searchHistoryContainer.visibility = View.GONE
        when (type) {
            MESSAGE_CONTENT -> {
                rvSongs.visibility = View.VISIBLE
                placeholderNoResults.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
            MESSAGE_NO_RESULTS -> {
                rvSongs.visibility = View.GONE
                placeholderNoResults.visibility = View.VISIBLE
                placeholderError.visibility = View.GONE
            }
            MESSAGE_ERROR -> {
                rvSongs.visibility = View.GONE
                placeholderNoResults.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE
            }
        }
    }

    private fun performSearch(query: String) {
        iTunesService.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null) {
                        trackList.clear()
                        if (searchResponse.results.isNotEmpty()) {
                            trackList.addAll(searchResponse.results)
                            trackAdapter.notifyDataSetChanged()
                            showMessage(MESSAGE_CONTENT)
                        } else {
                            showMessage(MESSAGE_NO_RESULTS)
                        }
                    } else {
                        showMessage(MESSAGE_ERROR)
                    }
                } else {
                    showMessage(MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showMessage(MESSAGE_ERROR)
            }
        })
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