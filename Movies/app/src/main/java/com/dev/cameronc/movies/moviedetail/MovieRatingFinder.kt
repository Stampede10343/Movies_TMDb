package com.dev.cameronc.movies.moviedetail

import android.content.Context
import com.dev.cameronc.moviedb.data.movie.detail.ReleaseResult
import com.dev.cameronc.movies.R
import javax.inject.Inject

open class MovieRatingFinder @Inject constructor(private val context: Context?) {

    open fun getUSRatingOrEmpty(releaseDates: List<ReleaseResult>): String {
        val usRelease: ReleaseResult? = releaseDates.firstOrNull { it.iso31661 == "US" }
        return if (usRelease != null && usRelease.releaseDates.isNotEmpty() && usRelease.releaseDates.firstOrNull()?.certification?.isNotBlank() == true) {
            usRelease.releaseDates[0].certification
        } else context?.resources?.getString(R.string.no_rating) ?: ""
    }
}