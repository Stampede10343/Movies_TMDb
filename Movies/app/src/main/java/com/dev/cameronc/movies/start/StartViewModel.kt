package com.dev.cameronc.movies.start

import android.content.SharedPreferences
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.movies.ViewModel
import com.dev.cameronc.movies.model.MovieRepository
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class StartViewModel @Inject constructor(private val movieRepository: MovieRepository, private val preferences: SharedPreferences) : ViewModel() {
    private val subscriptions = CompositeDisposable()
    private val currentPageSubject = BehaviorSubject.create<Int>()
    private val upcomingMoviesSubject = BehaviorSubject.create<MutableList<UpcomingMovie>>()
    private val searchResults: BehaviorSubject<List<SearchResult>> = BehaviorSubject.create()
    private val searchQuerySubject: BehaviorSubject<String> = BehaviorSubject.create()

    init {
        subscriptions.add(currentPageSubject
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMap { currentPage ->
                    movieRepository.getUpcomingMovies(currentPage.toString()).map { it.toMutableList() }
                }
                .takeUntil { currentPageSubject.value == 10 }
                .subscribe({ movies ->
                    upcomingMoviesSubject.onNext(movies)
                }, { error -> Timber.e(error) }))

        subscriptions.add(searchQuerySubject
                .debounce(250, TimeUnit.MILLISECONDS)
                .filter { it.isNotEmpty() }
                .flatMap { movieRepository.searchAll(it) }
                .map { searchResponse -> searchResponse.results.sortedByDescending { it.popularity } }
                .map { sortedResults ->
                    sortedResults.map {
                        val type = if (it.mediaType == "movie") MediaType.Movie else if (it.mediaType == "tv") MediaType.Television else MediaType.Person
                        val imagePath = if (it.posterPath.isNullOrBlank()) it.profilePath else it.posterPath
                        SearchResult(it.id, imagePath, if (it.title.isNullOrBlank()) it.name!! else it.title!!, type)
                    }
                }
                .subscribe({ results ->
                    searchResults.onNext(results)
                }, { error -> Timber.e(error) }))
    }

    fun getUpcomingMovies(page: Int = 1): Observable<MutableList<UpcomingMovie>> {
        currentPageSubject.onNext(page)
        return upcomingMoviesSubject
    }

    fun loadMoreMovies() {
        currentPageSubject.onNext(currentPageSubject.value!! + 1)
    }

    fun onSearchEntered(query: String): Observable<SearchResponse> = movieRepository.searchMovies(query)

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

    fun queryTextChanged(query: String?) {
        if (query != null) searchQuerySubject.onNext(query)
    }

    fun searchResults(): Observable<List<SearchResult>> = searchResults
}