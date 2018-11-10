package com.dev.cameronc.movies.moviedetail

import android.arch.lifecycle.ViewModel
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.MovieRepo
import com.dev.cameronc.movies.model.movie.MovieReview
import io.reactivex.Observable
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(private val movieRepo: MovieRepo) : ViewModel() {

    fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> =
            movieRepo.getMovieDetails(movieId)

    fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> =
            movieRepo.getMovieCredits(movieId)

    fun getRelatedMovies(movieId: Long): Observable<SimilarMoviesResponse> =
            movieRepo.getSimilarMovies(movieId)

    fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> =
            movieRepo.getMovieReviews(movieId)
}