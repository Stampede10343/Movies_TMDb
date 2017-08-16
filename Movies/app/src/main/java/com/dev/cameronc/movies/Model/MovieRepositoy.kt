package com.dev.cameronc.movies.Model

import io.reactivex.Observable
import io.reactivex.ObservableSource

interface MovieRepositoy
{
    fun getUpcomingMovies(page: String): Observable<List<MovieResponseItem>>
    fun saveMovies(movies: List<MovieResponseItem>)
    fun getConfiguration(): ObservableSource<out ConfigurationResponse>
}