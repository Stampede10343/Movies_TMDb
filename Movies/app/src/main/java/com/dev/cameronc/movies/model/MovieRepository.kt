package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.data.ConfigurationResponse
import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.MovieVideo
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.reactivex.Observable
import io.reactivex.ObservableSource

interface MovieRepository {
    fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>>
    fun saveMovies(movies: List<MovieResponseItem>)
    fun getConfiguration(): ObservableSource<out ConfigurationResponse>
    fun searchMovies(query: String): Observable<SearchResponse>
    fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse>
    fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse>
    fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse>
    fun searchAll(query: String): Observable<MultiSearchResponse>
    fun getMovieReviews(movieId: Long): Observable<List<MovieReview>>
    fun getVideosForMovie(movieId: Long): Observable<List<MovieVideo>>
    fun getMovieImages(movieId: Long): Observable<List<String>>
}