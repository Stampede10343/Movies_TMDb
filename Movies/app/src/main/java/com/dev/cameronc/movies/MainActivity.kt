package com.dev.cameronc.movies

import android.os.Bundle
import com.dev.cameronc.movies.start.StartScreen
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Navigator.install(this, findViewById(android.R.id.content), History.single(StartScreen.StartKey()))
    }

    override fun onBackPressed() {
        if (Navigator.onBackPressed(this))
        else super.onBackPressed()
    }
}