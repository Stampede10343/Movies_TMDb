package com.dev.cameronc.movies

import android.os.Bundle
import android.view.ViewGroup
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.movies.start.StartScreen
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MoviesApp.activityComponent = getAppComponent().plusActivity().activity(this).build()
        Navigator.install(this, findViewById(android.R.id.content), History.single(StartScreen.StartKey()))
    }

    override fun onBackPressed() {
        val currentScreen = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as BaseScreen
        if (currentScreen.handleBackPressed()) {
        } else {
            if (Navigator.onBackPressed(this)) else super.onBackPressed()
        }
    }
}