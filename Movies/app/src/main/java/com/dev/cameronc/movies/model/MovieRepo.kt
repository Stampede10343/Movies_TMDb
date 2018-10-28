package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.*
import com.dev.cameronc.movies.model.movie.MovieMapper
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(private val movieDbApi: MovieDbApi,
                                    private val boxStore: BoxStore,
                                    private val movieMapper: MovieMapper) : MovieRepository {
    private val movieItemBox: Box<UpcomingMovie> = boxStore.boxFor(UpcomingMovie::class.java)
    private var genres: MutableMap<Int, String>? = null
    private var upcomingMovies: MutableList<MovieResponseItem> = emptyList<MovieResponseItem>().toMutableList()
    //    private val moviesSubject: BehaviorSubject<List<MovieResponseItem>> = BehaviorSubject.create()
    private val getMoviesSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val moviesSubject: BehaviorSubject<List<UpcomingMovie>> = BehaviorSubject.create()
    private var moviesSubscription: Disposable

    init {
        moviesSubscription = getMoviesSubject
                .distinct()
                .flatMap { page ->
                    val remoteMovies = getMoviesFromApi(page)
                    val localMovies = getMoviesFromBox(page)
                    Observable.concat<List<UpcomingMovie>>(localMovies, remoteMovies)
                }
                .subscribeOn(Schedulers.io())
                .map {
                    val allMoviesCached = moviesSubject.value?.toMutableSet()
                            ?: emptySet<UpcomingMovie>().toMutableSet()
                    allMoviesCached.addAll(it)
                    allMoviesCached.toList()
                }
                .doOnNext { movieItemBox.put(it) }
                .subscribe({ movies ->
                    moviesSubject.onNext(movies)
                }, { error -> Timber.e(error) })
    }

    override fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>> {
        getMoviesSubject.onNext(page)
        return moviesSubject
    }

    private fun getMoviesFromBox(page: String): Observable<MutableList<UpcomingMovie>> =
            Observable.just(movieItemBox.all)

    private fun getMoviesFromApi(page: String): Observable<List<UpcomingMovie>> =
            movieDbApi.getUpcomingMovies(page).map { it.results }
                    .map { movies ->
                        movies.map { movieResponseItem -> movieMapper.mapMovieResponseToUpcomingMovie(movieResponseItem) }
                    }

    private fun addGenres(results: List<MovieResponseItem>, genres: Map<Int, String>) {
        //results.forEach { mov -> mov.genreIds.forEach { mov.addGenre(genres[it]!!) } }
    }

    private fun genreMapper(): Observable<Map<Int, String>> {
        if (genres == null) {
            genres = HashMap()
            return movieDbApi.getGenres().map { it.genres.forEach { addGenreIntoMap(it) } }.map { genres }
        } else {
            return Observable.just(genres)
        }
    }

    private fun addGenreIntoMap(genre: Genre) {
        genres?.put(genre.id, genre.name)
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
        movieItemBox.put(newMovies)
    }

    override fun getConfiguration(): ObservableSource<out ConfigurationResponse> =
            movieDbApi.getConfiguration()

    override fun searchMovies(query: String): Observable<SearchResponse> = movieDbApi.search(query)

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> =
            movieDbApi.movieDetails(movieId).toObservable()

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> = movieDbApi.movieCredits(movieId).toObservable()

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> = movieDbApi.similarMovies(movieId).toObservable()
}