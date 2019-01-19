package com.dev.cameronc.movies

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.dev.cameronc.androidutilities.KeyboardHelper
import com.dev.cameronc.androidutilities.SnackBarHelper
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
    // Injected so that we start listening for network connectivity immediately
    @Suppress("unused")
    @Inject
    lateinit var snackBarHelper: SnackBarHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MoviesApp.activityComponent = getAppComponent().plusActivity().activity(this).build()
        MoviesApp.activityComponent.inject(this)
        themeManager.setTheme()

        Navigator.install(this, findViewById(R.id.app_content), History.single(StartScreen.StartKey()))

    }

    override fun onStop() {
        super.onStop()
        keyboardHelper.clearListener()
    }

    override fun onBackPressed() {
        val currentScreen = findViewById<ViewGroup>(R.id.app_content).getChildAt(0) as BaseScreen
        if (currentScreen.handleBackPressed()) {
        } else {
            if (Navigator.onBackPressed(this)) else super.onBackPressed()
        }
    }
}