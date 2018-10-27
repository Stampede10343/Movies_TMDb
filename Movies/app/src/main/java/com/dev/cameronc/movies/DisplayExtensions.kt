package com.dev.cameronc.movies

fun Int.toDp() = (this * MoviesApp.app.resources.displayMetrics.density).toInt()