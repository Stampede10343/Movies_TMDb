package com.dev.cameronc.movies.options

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.dev.cameronc.movies.R
import javax.inject.Inject

class ThemeManager @Inject constructor(private val activity: AppCompatActivity, private val preferences: SharedPreferences) {

    fun setTheme() {
        activity.setTheme(if (preferences.getBoolean(DARK_THEME, true)) R.style.AppTheme else R.style.AppTheme_Light)
    }

    companion object {
        const val DARK_THEME = "dark_theme"
    }
}