package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.ConfigurationResponse
import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.movie.MovieMapper
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.MovieVideo
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
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
    private val movieReviewCache: MutableMap<Long, List<MovieReview>> = emptyMap<Long, List<MovieReview>>().toMutableMap()
    private val similarMovieCache: MutableMap<Long, SimilarMoviesResponse> = emptyMap<Long, SimilarMoviesResponse>().toMutableMap()
    private val movieDetailsCache: MutableMap<Long, MovieDetailsResponse> = emptyMap<Long, MovieDetailsResponse>().toMutableMap()
    private val movieCreditsCache: MutableMap<Long, MovieCreditsResponse> = emptyMap<Long, MovieCreditsResponse>().toMutableMap()

    init {
        moviesSubscription = getMoviesSubject
                .distinct()
                .flatMap { page ->
                    val remoteMovies = getMoviesFromApi(page)
                    val localMovies = getMoviesFromBox()
                    Observable.concat<List<UpcomingMovie>>(localMovies, remoteMovies)
                            .firstElement()
                            .toObservable()
                }
                .doOnNext { analyticTracker.trackEvent("Movies loaded. Page: ${getMoviesSubject.value}. Count: ${it.size}") }
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
                    .map {
                        val allMoviesCached = moviesSubject.value?.toMutableSet()
                                ?: emptySet<UpcomingMovie>().toMutableSet()
                        allMoviesCached.addAll(it)
                        allMoviesCached.toList()
                    }
                    .observeOn(AndroidSchedulers.mainThread())

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

    override fun searchMovies(query: String): Observable<SearchResponse> =
            movieDbApi.search(query)
                    .doOnNext { analyticTracker.trackEvent("Search Movies: $query") }
                    .doOnError { Timber.e(it) }

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> {
        val remoteMovieDetails = movieDbApi.movieDetails(movieId).toObservable()
                .doOnNext { analyticTracker.trackEvent("Get Movie Details: $movieId") }
                .doOnNext { movieDetailsCache[movieId] = it }
                .doOnError { Timber.e(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val cachedDetails = movieDetailsCache[movieId]
        val cachedDetailsObservable = if (cachedDetails == null) Observable.empty<MovieDetailsResponse>() else Observable.just(cachedDetails)

        return Observable.concat(cachedDetailsObservable, remoteMovieDetails)
                .firstElement()
                .toObservable()
    }

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> {
        val remoteCredits = movieDbApi.movieCredits(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get Movie Credits: $movieId") }
                .doOnNext { movieCreditsCache[movieId] = it }
                .doOnError { Timber.e(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val cachedCredits = movieCreditsCache[movieId]
        val cacheCreditsObservable = if (cachedCredits == null) Observable.empty<MovieCreditsResponse>() else Observable.just(cachedCredits)

        return Observable.concat(cacheCreditsObservable, remoteCredits)
                .firstElement()
                .toObservable()
    }

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> {
        val remoteSimilarMovies = movieDbApi.similarMovies(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get SimilarMovies $movieId. Count: ${it.results.size}") }
                .doOnNext { similarMovieCache[movieId] = it }
                .doOnError { Timber.e(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val cachedSimilarMovies = similarMovieCache[movieId]
        val cachedSimilarMoviesObservable = if (cachedSimilarMovies == null) Observable.empty<SimilarMoviesResponse>() else Observable.just(cachedSimilarMovies)

        return Observable.concat(cachedSimilarMoviesObservable, remoteSimilarMovies)
                .firstElement()
                .toObservable()
    }

    override fun searchAll(query: String): Observable<MultiSearchResponse> =
            movieDbApi.searchMulti(query)
                    .toObservable()
                    .doOnNext { analyticTracker.trackEvent("Search: $query. Count: ${it.results.size}") }
                    .doOnError { Timber.e(it) }

    override fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> {
        val remoteReviews = movieDbApi.movieReview(movieId)
                .toObservable()
                .doOnNext { analyticTracker.trackEvent("Get Movie Reviews: $movieId") }
                .map { movieResponse -> movieResponse.results.map { movieMapper.mapMovieReviewResponseToMovieReview(it) } }
                .doOnNext { movieReviewCache[movieId] = it }
                .doOnError { Timber.e(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val cachedReviews: List<MovieReview>? = movieReviewCache[movieId]
        val cachedReviewObservable: Observable<List<MovieReview>> =
                if (cachedReviews == null) Observable.empty<List<MovieReview>>() else Observable.just(cachedReviews)

        return Observable.concat(cachedReviewObservable, remoteReviews)
                .firstElement()
                .toObservable()
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