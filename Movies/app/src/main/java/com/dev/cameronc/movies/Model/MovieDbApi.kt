package com.dev.cameronc.movies.Model

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieDbApi
{
    @GET("configuration")
    fun getConfiguration(): Observable<ConfigurationResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: String): Observable<UpcomingMovieResponse>

    @GET("genre/movie/list")
    fun getGenres(): Observable<GenreResponse>
}