package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.androidutilities.network.ObservableConnectivityManager
import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.movie.MovieMapper
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.MovieVideo
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkMovieDataSource @Inject constructor(private val movieDbApi: MovieDbApi,
                                                 private val connectivityManager: ObservableConnectivityManager,
                                                 private val movieMapper: MovieMapper,
                                                 private val preferences: SharedPreferences,
                                                 private val analyticTracker: AnalyticTrackingHelper) : MovieDataSource {

    override fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>> {
        return movieDbApi.getUpcomingMovies(page)
                .subscribeOn(Schedulers.io())
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else {
                            Observable.error(exception)
                        }
                    }
                }
                .map { it.results }
                .map { movies -> movies.map { movieResponseItem -> movieMapper.mapMovieResponseToUpcomingMovie(movieResponseItem) } }
                .doOnNext { preferences.edit().putLong("movie_save_time", DateTime.now().millis).apply() }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchMovies(query: String): Observable<SearchResponse> {
        return movieDbApi.search(query)
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else {
                            Observable.error(exception)
                        }
                    }
                }
                .doOnNext { analyticTracker.trackEvent("Search Movies: $query") }
                .doOnError { Timber.v(it) }
    }

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> {
        return movieDbApi.movieDetails(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get Movie Details: $movieId") }
                .doOnError { Timber.e(it) }
                .subscribeOn(Schedulers.io())
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else {
                            Observable.error(exception)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> {
        return movieDbApi.movieCredits(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get Movie Credits: $movieId") }
                .doOnError { Timber.v(it) }
                .subscribeOn(Schedulers.io())
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else {
                            Observable.error(exception)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> {
        return movieDbApi.similarMovies(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get SimilarMovies $movieId. Count: ${it.results.size}") }
                .doOnError { Timber.v(it) }
                .subscribeOn(Schedulers.io())
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else {
                            Observable.error(exception)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchAll(query: String): Observable<MultiSearchResponse> {
        return movieDbApi.searchMulti(query)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else Observable.error(exception)
                    }
                }
                .doOnNext { analyticTracker.trackEvent("Search: $query. Count: ${it.results.size}") }
                .doOnError { Timber.e(it) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> {
        return movieDbApi.movieReview(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get Movie Reviews: $movieId") }
                .map { movieResponse -> movieResponse.results.map { movieMapper.mapMovieReviewResponseToMovieReview(it) } }
                .doOnError { Timber.e(it) }
                .subscribeOn(Schedulers.io())
                .retryWhen { throwableObservable ->
                    throwableObservable.flatMap { exception ->
                        if (exception is UnknownHostException) {
                            connectivityManager.connectivityAvailable()
                        } else {
                            Observable.error(exception)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getVideosForMovie(movieId: Long): Observable<List<MovieVideo>> =
            movieDbApi.videosForMovie(movieId)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Get Videos for Movie: $movieId. Count: ${it.results.size}") }
                    .doOnError { Timber.e(it) }
                    .map { videosResponse -> videosResponse.results.map { MovieVideo(it.id, it.site, it.key) } }

    override fun getMovieImages(movieId: Long): Observable<List<String>> =
            movieDbApi.imagesForMovie(movieId)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Get Images for Movie: $movieId") }
                    .doOnError { Timber.e(it) }
                    .map { imagesResponse -> imagesResponse.backdrops.map { it.filePath } }
}