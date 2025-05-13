package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings2)
        val backFromSettingsButton: Button = findViewById(R.id.backFromSettingsButton)

        backFromSettingsButton.setOnClickListener {
            val  backFromSettingsActivity = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(backFromSettingsActivity)
        }
    }
}