package com.dev.cameronc.movies

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.dev.cameronc.movies.di.prod.AppComponent

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    fun getApp(): MoviesApp = application as MoviesApp

    fun getAppComponent(): AppComponent = getApp().getAppComponent()
}