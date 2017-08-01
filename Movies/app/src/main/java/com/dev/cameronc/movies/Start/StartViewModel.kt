package com.dev.cameronc.movies.Start

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.dev.cameronc.movies.Model.ConfigurationResponse
import com.dev.cameronc.movies.Model.MovieDbApi
import com.dev.cameronc.movies.Model.UpcomingMovieResponse
import com.dev.cameronc.movies.MovieImageDownloader
import com.google.gson.Gson
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject


class StartViewModel @Inject constructor(val moviesApi: MovieDbApi, val preferences: SharedPreferences, val imageDownloader: MovieImageDownloader) : ViewModel()
{
    fun getUpcomingMovies(page: Int = 1): Observable<UpcomingMovieResponse>
    {
        getConfiguration()
        return moviesApi.getUpcomingMovies(page.toString()).subscribeOn(Schedulers.io())
    }

    fun getConfiguration()
    {
        Observable.concat(moviesApi.getConfiguration(), getConfigFromPreferences()).subscribeOn(
                Schedulers.io()).filter { it.images.baseUrl.isNotEmpty() }.subscribe { response ->
            val json: String = Gson().toJson(response)
            if(preferences.getString("configuration", "").isEmpty())
            {
                preferences.edit().putString("configuration", json).apply()
            }
            imageDownloader.init(response)
        }
    }

    private fun getConfigFromPreferences(): Observable<ConfigurationResponse>
    {
        val json = preferences.getString("configuration", "")
        return Observable.just(Gson().fromJson(json, ConfigurationResponse::class.java))
    }
}