package com.dev.cameronc.movies

import android.support.v7.app.AppCompatActivity
import com.dev.cameronc.movies.di.AppComponent

open class BaseActivity: AppCompatActivity()
{
    fun getApp(): MoviesApp = application as MoviesApp

    fun getAppComponent(): AppComponent = getApp().getAppComponent()
}