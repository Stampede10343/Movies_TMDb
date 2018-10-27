package com.dev.cameronc.movies.start

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.dev.cameronc.moviedb.data.ConfigurationResponse
import com.dev.cameronc.moviedb.data.MovieResponseItem
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.movies.model.MovieRepository
import com.dev.cameronc.movies.MovieImageDownloader
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class StartViewModel @Inject constructor(private val movieRepository: MovieRepository, private val preferences: SharedPreferences,
                                         private val imageDownloader: MovieImageDownloader) : ViewModel() {
    fun getUpcomingMovies(page: Int = 1): Observable<MutableList<MovieResponseItem>> {
        return movieRepository.getUpcomingMovies(page.toString())
                .subscribeOn(Schedulers.io())
                .map { it.toMutableList() }
    }

    private fun getConfiguration() {
        Observable.concat(movieRepository.getConfiguration(), getConfigFromPreferences())
                .subscribeOn(Schedulers.io())
                .filter { it.images.baseUrl.isNotEmpty() }
                .subscribe { response ->
                    val json: String = Gson().toJson(response)
                    if (preferences.getString("configuration", "").isEmpty()) {
                        preferences.edit()
                                .putString("configuration", json)
                                .apply()
                    }
                }
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
}