package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.*
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(private val movieDbApi: MovieDbApi, private val boxStore: BoxStore) : MovieRepository {
    private val movieItemBox: Box<MovieResponseItem> = boxStore.boxFor(MovieResponseItem::class.java)
    private var genres: MutableMap<Int, String>? = null
    private var upcomingMovies: MutableList<MovieResponseItem> = emptyList<MovieResponseItem>().toMutableList()
    private val moviesSubject: BehaviorSubject<List<MovieResponseItem>> = BehaviorSubject.create()
    private val getMoviesSubject: PublishSubject<String> = PublishSubject.create()
    private var moviesSubscription: Disposable? = null

    override fun getUpcomingMovies(page: String): Observable<List<MovieResponseItem>> {
        if (moviesSubscription == null) {
            moviesSubscription = getMoviesSubject.flatMap { getMoviesFromApi(it) }
                    .distinctUntilChanged()
                    .map {
                        val savedMoves = movieItemBox.all
                        val newMovies = emptyList<MovieResponseItem>().toMutableList()
                        for (newMovie in it) {
                            if (!savedMoves.contains(newMovie)) newMovies.add(newMovie)
                        }
                        movieItemBox.put(newMovies)
                        val allMovies = savedMoves.toMutableList()
                        allMovies.addAll(newMovies)
                        allMovies
                    }
                    .startWith(getMoviesFromBox("1"))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ movies ->
                        moviesSubject.onNext(movies)
                    }, { err -> Timber.e(err) })
        }
        getMoviesSubject.onNext(page)
        return moviesSubject
    }

    private fun getMoviesFromBox(
            page: String): Observable<MutableList<MovieResponseItem>> = Observable.just(movieItemBox.all)

    private fun getMoviesFromApi(page: String): Observable<List<MovieResponseItem>> {
        val upcomingMovies: Observable<UpcomingMovieResponse> = movieDbApi.getUpcomingMovies(page).subscribeOn(Schedulers.io())

        return Observable.zip<UpcomingMovieResponse, Map<Int, String>, List<MovieResponseItem>>(upcomingMovies, genreMapper(), BiFunction { response, genres ->
            addGenres(response.results, genres)
            response.results
        }).subscribeOn(Schedulers.computation())
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
        val newMovies = movies.toMutableList()
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