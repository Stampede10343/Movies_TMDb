package com.dev.cameronc.movies

import android.content.Context
import com.dev.cameronc.movies.di.AppComponent

fun Int.toDp() = (this * MoviesApp.app.resources.displayMetrics.density).toInt()

fun Context.appComponent(): AppComponent = (applicationContext as MoviesApp).getAppComponent()