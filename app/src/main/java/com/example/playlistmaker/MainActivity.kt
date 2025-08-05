package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
// import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        
        setTheme(R.style.Theme_PlaylistMaker_Main)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById(R.id.searchButton)
        val mediatecaButton: Button = findViewById(R.id.mediatecaButton)
        val optionsButton: Button = findViewById(R.id.optionsButton)

        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Toast.makeText(this@MainActivity, "Нажата кнопка 'Поиск'", Toast.LENGTH_SHORT).show()
                val searchActivity = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchActivity)
            }
        })

        mediatecaButton.setOnClickListener {
            // Toast.makeText(this@MainActivity, "Нажата кнопка 'Медиатека'", Toast.LENGTH_SHORT).show()
            val mediaIntent = Intent(this, MediatecaActivity::class.java)
            startActivity(mediaIntent)
        }

        optionsButton.setOnClickListener {
            // Toast.makeText(this@MainActivity, "Нажата кнопка 'Настройки'", Toast.LENGTH_SHORT).show()
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}