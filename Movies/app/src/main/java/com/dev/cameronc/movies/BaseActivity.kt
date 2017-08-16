package com.dev.cameronc.movies

import android.support.v7.app.AppCompatActivity

open class BaseActivity: AppCompatActivity()
{
    fun getApp(): MoviesApp
    {
        return application as MoviesApp
    }
}