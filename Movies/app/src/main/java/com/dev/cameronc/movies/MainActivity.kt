package com.dev.cameronc.movies

import android.os.Bundle
import android.view.ViewGroup
import com.dev.cameronc.androidutilities.KeyboardHelper
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.movies.options.ThemeManager
import com.dev.cameronc.movies.start.StartScreen
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var themeManager: ThemeManager
    @Inject
    lateinit var keyboardHelper: KeyboardHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        MoviesApp.activityComponent = getAppComponent().plusActivity().activity(this).build()
        MoviesApp.activityComponent.inject(this)
        themeManager.setTheme()
        Navigator.install(this, findViewById(android.R.id.content), History.single(StartScreen.StartKey()))
    }

    override fun onStop() {
        super.onStop()
        keyboardHelper.clearListener()
    }

    override fun onBackPressed() {
        val currentScreen = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as BaseScreen
        if (currentScreen.handleBackPressed()) {
        } else {
            if (Navigator.onBackPressed(this)) else super.onBackPressed()
        }
    }
}