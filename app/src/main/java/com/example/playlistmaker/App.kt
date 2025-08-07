package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

const val THEME_PREFS = "theme_prefs"
const val THEME_KEY = "theme_key"
class App : Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(THEME_PREFS, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        setTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPrefs = getSharedPreferences(THEME_PREFS, MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(THEME_KEY, darkTheme).apply()

        setTheme(darkTheme)
    }

    private fun setTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}