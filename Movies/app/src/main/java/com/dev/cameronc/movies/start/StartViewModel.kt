package com.dev.cameronc.movies.start

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.dev.cameronc.moviedb.data.ConfigurationResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.movies.model.MovieRepository
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class StartViewModel @Inject constructor(private val movieRepository: MovieRepository, private val preferences: SharedPreferences) : ViewModel() {
    private val subscriptions = CompositeDisposable()
    private val currentPageSubject = BehaviorSubject.create<Int>()
    private val upcomingMoviesSubject = BehaviorSubject.create<MutableList<UpcomingMovie>>()

    init {
        subscriptions.add(currentPageSubject
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMap { currentPage ->
                    movieRepository.getUpcomingMovies(currentPage.toString())
                            .map { it.toMutableList() }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil { currentPageSubject.value == 10 }
                .subscribe({ movies ->
                    upcomingMoviesSubject.onNext(movies)
                }, { error -> Timber.e(error) }))
    }

    fun getUpcomingMovies(page: Int = 1): Observable<MutableList<UpcomingMovie>> {
        currentPageSubject.onNext(page)
        return upcomingMoviesSubject
    }

    fun loadMoreMovies() {
        currentPageSubject.onNext(currentPageSubject.value!! + 1)
    }

    private fun getConfiguration() {
        subscriptions.add(Observable.concat(movieRepository.getConfiguration(), getConfigFromPreferences())
                .subscribeOn(Schedulers.io())
                .filter { it.images.baseUrl.isNotEmpty() }
                .subscribe { response ->
                    val json: String = Gson().toJson(response)
                    if (preferences.getString("configuration", "").isNullOrEmpty()) {
                        preferences.edit()
                                .putString("configuration", json)
                                .apply()
                    }
                })
    }

    private fun getConfigFromPreferences(): Observable<ConfigurationResponse> {
        val json = preferences.getString("configuration", "")
        val response: ConfigurationResponse? = Gson().fromJson(json, ConfigurationResponse::class.java)
        if (response == null) {
            return Observable.empty()
        } else {
            return Observable.just(response)
        }
    }

    fun onSearchEntered(query: String): Observable<SearchResponse> {
        return movieRepository.searchMovies(query)
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.dispose()
    }
}