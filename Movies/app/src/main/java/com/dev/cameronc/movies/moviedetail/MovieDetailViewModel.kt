package com.dev.cameronc.movies.moviedetail

import android.arch.lifecycle.ViewModel
import com.dev.cameronc.moviedb.data.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.SimilarMoviesResponse
import com.dev.cameronc.movies.model.MovieRepo
import io.reactivex.Observable
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(private val movieRepo: MovieRepo) : ViewModel() {

    fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> =
            movieRepo.getMovieDetails(movieId)

    fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> =
            movieRepo.getMovieCredits(movieId)

    fun getRelatedMovies(movieId: Long): Observable<SimilarMoviesResponse> =
            movieRepo.getSimilarMovies(movieId)
}