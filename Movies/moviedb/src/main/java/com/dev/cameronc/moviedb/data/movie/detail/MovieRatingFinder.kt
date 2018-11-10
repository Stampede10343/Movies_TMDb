package com.dev.cameronc.moviedb.data.movie.detail

import javax.inject.Inject

class MovieRatingFinder @Inject constructor() {

    fun getUSRatingOrEmpty(releaseDates: List<ReleaseResult>): String {
        val usRelease: ReleaseResult? = releaseDates.firstOrNull { it.iso31661 == "US" }
        return if (usRelease != null && usRelease.releaseDates.isNotEmpty() && usRelease.releaseDates.firstOrNull()?.certification?.isNotBlank() == true) {
            usRelease.releaseDates[0].certification
        } else ""
    }
}