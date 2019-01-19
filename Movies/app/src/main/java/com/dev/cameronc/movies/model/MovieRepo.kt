package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.di.Network
import com.dev.cameronc.movies.model.movie.MovieMapper
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.MovieVideo
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime
import org.joda.time.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(@Network private val networkDataSource: MovieDataSource,
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
                }
                .scan { previousMovies, newMovies ->
                    val allMovies = emptySet<UpcomingMovie>().toMutableSet()
                    allMovies.addAll(previousMovies)
                    allMovies.addAll(newMovies)

                    allMovies.toList()
                }
                .map { it.sortedByDescending { movies -> movies.popularity } }
                .doOnNext { analyticTracker.trackEvent("Movies loaded. Page: ${getMoviesSubject.value}. Count: ${it.size}") }
                .subscribe({ movies ->
                    moviesSubject.onNext(movies)
                }, { error -> Timber.e(error) })
    }

    private fun getMoviesFromApi(page: String): Observable<List<UpcomingMovie>> =
            networkDataSource.getUpcomingMovies(page)

    private fun getMoviesFromBox(): Observable<MutableList<UpcomingMovie>> {
        val lastSaveTime = LocalDate(preferences.getLong("movie_save_time", Long.MAX_VALUE))
        val yesterday = LocalDate.now().minusDays(1)
        return if (lastSaveTime.isBefore(yesterday)) {
            Observable.empty()
        } else {
            val allMovies = movieItemBox.all//.sortedByDescending { it.popularity }.toMutableList()
            if (allMovies.isNotEmpty()) Observable.just(allMovies) else Observable.empty()
        }
    }

    override fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>> {
        getMoviesSubject.onNext(page)
        return moviesSubject
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

    override fun searchMovies(query: String): Observable<SearchResponse> =
            networkDataSource.searchMovies(query)

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> {
        val remoteMovieDetails = networkDataSource.getMovieDetails(movieId)
                .doOnNext { movieDetailsCache[movieId] = it }

        val cachedDetails = movieDetailsCache[movieId]
        val cachedDetailsObservable = if (cachedDetails == null) Observable.empty<MovieDetailsResponse>() else Observable.just(cachedDetails)

        return Observable.concat(cachedDetailsObservable, remoteMovieDetails)
                .firstElement()
                .toObservable()

    }

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> {
        val remoteCredits = networkDataSource.getMovieCredits(movieId)
                .doOnNext { movieCreditsCache[movieId] = it }

        val cachedCredits = movieCreditsCache[movieId]
        val cacheCreditsObservable = if (cachedCredits == null) Observable.empty<MovieCreditsResponse>() else Observable.just(cachedCredits)

        return Observable.concat(cacheCreditsObservable, remoteCredits)
                .firstElement()
                .toObservable()
    }

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> {
        val remoteSimilarMovies = networkDataSource.getSimilarMovies(movieId)
                .doOnNext { similarMovieCache[movieId] = it }

        val cachedSimilarMovies = similarMovieCache[movieId]
        val cachedSimilarMoviesObservable = if (cachedSimilarMovies == null) Observable.empty<SimilarMoviesResponse>() else Observable.just(cachedSimilarMovies)

        return Observable.concat(cachedSimilarMoviesObservable, remoteSimilarMovies)
                .firstElement()
                .toObservable()
    }

    override fun searchAll(query: String): Observable<MultiSearchResponse> =
            networkDataSource.searchAll(query)

    override fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> {
        val remoteReviews = networkDataSource.getMovieReviews(movieId)
                .doOnNext { movieReviewCache[movieId] = it }

        val cachedReviews: List<MovieReview>? = movieReviewCache[movieId]
        val cachedReviewObservable: Observable<List<MovieReview>> =
                if (cachedReviews == null) Observable.empty<List<MovieReview>>() else Observable.just(cachedReviews)

        return Observable.concat(cachedReviewObservable, remoteReviews)
                .firstElement()
                .toObservable()
    }

    override fun getVideosForMovie(movieId: Long): Observable<List<MovieVideo>> =
            networkDataSource.getVideosForMovie(movieId)

    override fun getMovieImages(movieId: Long): Observable<List<String>> =
            networkDataSource.getMovieImages(movieId)
}