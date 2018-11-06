package com.dev.cameronc.movies.options

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import kotlinx.android.synthetic.main.theme_picker_dialog.*
import javax.inject.Inject

class ThemePickerFragment : DialogFragment() {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.theme_picker_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MoviesApp.activityComponent.inject(this)

        dark_theme_switch.isChecked = preferences.getBoolean("dark_theme", true)
        dark_theme_switch.setOnCheckedChangeListener { _, isChecked ->
            val darkThemeEnabled = preferences.getBoolean(ThemeManager.DARK_THEME, true)
            if (darkThemeEnabled != isChecked) {
                preferences.edit()
                        .putBoolean("dark_theme", isChecked)
                        .apply()
                view.post { activity?.recreate() }
            }
        }
    }
}