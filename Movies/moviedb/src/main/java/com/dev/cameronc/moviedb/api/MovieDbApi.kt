package com.dev.cameronc.moviedb.api

import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.actor.ActorCreditsResponse
import com.dev.cameronc.moviedb.data.actor.ActorDetails
import com.dev.cameronc.moviedb.data.movie.UpcomingMovieResponse
import com.dev.cameronc.moviedb.data.movie.detail.*
import com.dev.cameronc.moviedb.data.movie.detail.video.MovieVideosResponse
import com.dev.cameronc.moviedb.data.tv.ActorTvCredits
import com.dev.cameronc.moviedb.data.tv.TvSeriesDetails
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDbApi {
    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: String, @Query("language") language: String = "en-US"): Observable<UpcomingMovieResponse>

    @GET("genre/movie/list")
    fun getGenres(): Observable<GenreResponse>

    @GET("search/movie")
    fun search(@Query("query") query: String): Observable<SearchResponse>

    @GET("movie/{movie_id}")
    fun movieDetails(@Path("movie_id") movieId: Long,
                     @Query("append_to_response") releaseDates: String = "release_dates",
                     @Query("language") language: String = "en-US"): Single<MovieDetailsResponse>

    @GET("movie/{movie_id}/credits")
    fun movieCredits(@Path("movie_id") movieId: Long): Single<MovieCreditsResponse>

    @GET("movie/{movie_id}/similar")
    fun similarMovies(@Path("movie_id") movieId: Long): Single<SimilarMoviesResponse>

    @GET("person/{person_id}")
    fun actorDetails(@Path("person_id") tmdbActorId: Long): Single<ActorDetails>

    @GET("person/{person_id}/movie_credits")
    fun actorMovieCredits(@Path("person_id") personId: Long): Single<ActorCreditsResponse>

    @GET("search/multi")
    fun searchMulti(@Query("query") query: String): Single<MultiSearchResponse>

    @GET("movie/{movie_id}/reviews")
    fun movieReview(@Path("movie_id") movieId: Long): Single<MovieReviewResponse>

    @GET("movie/{movie_id}/videos")
    fun videosForMovie(@Path("movie_id") movieId: Long): Single<MovieVideosResponse>

    @GET("movie/{movie_id}/images")
    fun imagesForMovie(@Path("movie_id") movieId: Long): Single<MovieImagesResponse>

    @GET("person/{person_id}/tv_credits")
    fun actorTvCredits(@Path("person_id") personId: Long): Single<Response<ActorTvCredits>>

    @GET("tv/{tv_id}")
    fun tvSeriesDetails(@Path("tv_id") seriesId: Long): Observable<TvSeriesDetails>
}