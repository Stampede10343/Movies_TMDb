package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.data.*
import io.reactivex.Observable
import io.reactivex.ObservableSource

interface MovieRepository {
    fun getUpcomingMovies(page: String): Observable<List<MovieResponseItem>>
    fun saveMovies(movies: List<MovieResponseItem>)
    fun getConfiguration(): ObservableSource<out ConfigurationResponse>
    fun searchMovies(query: String): Observable<SearchResponse>
    fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse>
    fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse>
    fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse>
}