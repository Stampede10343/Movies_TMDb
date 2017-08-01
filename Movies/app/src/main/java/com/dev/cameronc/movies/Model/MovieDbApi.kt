package com.dev.cameronc.movies.Model

import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface MovieDbApi
{
    @GET("configuration")
    fun getConfiguration(): Observable<ConfigurationResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: String): Observable<UpcomingMovieResponse>
}