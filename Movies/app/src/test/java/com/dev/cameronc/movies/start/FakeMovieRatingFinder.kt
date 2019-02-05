package com.dev.cameronc.movies.start

import com.dev.cameronc.moviedb.data.movie.detail.ReleaseResult
import com.dev.cameronc.movies.moviedetail.MovieRatingFinder

class FakeMovieRatingFinder : MovieRatingFinder(null) {

    override fun getUSRatingOrEmpty(releaseDates: List<ReleaseResult>): String = "fake release date"
}