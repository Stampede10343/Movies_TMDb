package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.*
import com.dev.cameronc.movies.model.movie.MovieMapper
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime
import org.joda.time.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(private val movieDbApi: MovieDbApi,
                                    boxStore: BoxStore,
                                    private val movieMapper: MovieMapper,
                                    private val analyticTracker: AnalyticTrackingHelper,
                                    private val preferences: SharedPreferences) : MovieRepository {

    private val movieItemBox: Box<UpcomingMovie> = boxStore.boxFor(UpcomingMovie::class.java)
    private val getMoviesSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val moviesSubject: BehaviorSubject<List<UpcomingMovie>> = BehaviorSubject.create()
    private var moviesSubscription: Disposable

    init {
        moviesSubscription = getMoviesSubject
                .distinct()
                .flatMap { page ->
                    val remoteMovies = getMoviesFromApi(page)
                    val localMovies = getMoviesFromBox()
                    Observable.concat<List<UpcomingMovie>>(localMovies, remoteMovies)
                }
                .doOnNext { analyticTracker.trackEvent("Movies loaded. Page: ${getMoviesSubject.value}. Count: ${it.size}") }
                .subscribeOn(Schedulers.io())
                .map {
                    val allMoviesCached = moviesSubject.value?.toMutableSet()
                            ?: emptySet<UpcomingMovie>().toMutableSet()
                    allMoviesCached.addAll(it)
                    allMoviesCached.toList()
                }
                .doOnNext {
                    preferences.edit().putLong("movie_save_time", DateTime.now().millis).apply()
                    movieItemBox.put(it)
                }
                .subscribe({ movies ->
                    moviesSubject.onNext(movies)
                }, { error -> Timber.e(error) })
    }

    override fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>> {
        getMoviesSubject.onNext(page)
        return moviesSubject
    }

    private fun getMoviesFromBox(): Observable<MutableList<UpcomingMovie>> {
        val lastSaveTime = LocalDate(preferences.getLong("movie_save_time", Long.MAX_VALUE))
        val yesterday = LocalDate.now().minusDays(1)
        return if (lastSaveTime.isBefore(yesterday)) {
            Observable.empty()
        } else {
            val allMovies = movieItemBox.all
            if (allMovies.isNotEmpty()) Observable.just(allMovies) else Observable.empty()
        }
    }

    private fun getMoviesFromApi(page: String): Observable<List<UpcomingMovie>> =
            movieDbApi.getUpcomingMovies(page)
                    .subscribeOn(Schedulers.io())
                    .map { it.results }
                    .map { movies ->
                        movies.map { movieResponseItem -> movieMapper.mapMovieResponseToUpcomingMovie(movieResponseItem) }
                    }

    override fun saveMovies(movies: List<MovieResponseItem>) {
        val allMovies = movieItemBox.all
        val newMovies = movies.asSequence()
                .toMutableList()
                .asSequence()
                .map { movieMapper.mapMovieResponseToUpcomingMovie(it) }
                .toMutableList()
        for (movie in allMovies) {
            newMovies.remove(movie)
        }
        preferences.edit().putLong("movie_save_time", DateTime.now().millis).apply()
        movieItemBox.put(newMovies)
    }

    override fun getConfiguration(): ObservableSource<out ConfigurationResponse> =
            movieDbApi.getConfiguration()

    override fun searchMovies(query: String): Observable<SearchResponse> = movieDbApi.search(query)
            .doOnNext { analyticTracker.trackEvent("Search Movies: $query") }

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> =
            movieDbApi.movieDetails(movieId).toObservable()
                    .doOnNext { analyticTracker.trackEvent("Get Movie Details: $movieId") }

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> =
            movieDbApi.movieCredits(movieId)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Get Movie Credits: $movieId") }

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> =
            movieDbApi.similarMovies(movieId)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Get SimilarMovies $movieId. Count: ${it.results.size}") }

    override fun searchAll(query: String): Observable<MultiSearchResponse> =
            movieDbApi.searchMulti(query)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Search: $query. Count: ${it.results.size}") }

    override fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> =
            movieDbApi.movieReview(movieId)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Get Movie Reviews: $movieId") }
                    .map { movieResponse -> movieResponse.results.map { movieMapper.mapMovieReviewResponseToMovieReview(it) } }
}