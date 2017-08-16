package com.dev.cameronc.movies.Start

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.dev.cameronc.movies.Model.ConfigurationResponse
import com.dev.cameronc.movies.Model.MovieResponseItem
import com.dev.cameronc.movies.Model.MovieRepositoy
import com.dev.cameronc.movies.MovieImageDownloader
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class StartViewModel @Inject constructor(val movieRepository: MovieRepositoy, val preferences: SharedPreferences,
                                         val imageDownloader: MovieImageDownloader) : ViewModel()
{
    fun getUpcomingMovies(page: Int = 1): Observable<MutableList<MovieResponseItem>>
    {
        getConfiguration()
        return movieRepository.getUpcomingMovies(page.toString()).subscribeOn(Schedulers.io()).map {
            movieRepository.saveMovies(it)
            it.toMutableList()
        }
    }

    fun getConfiguration()
    {
        Observable.concat(movieRepository.getConfiguration(), getConfigFromPreferences()).subscribeOn(
                Schedulers.io()).filter { it.images.baseUrl.isNotEmpty() }.subscribe { response ->
            val json: String = Gson().toJson(response)
            if (preferences.getString("configuration", "").isEmpty())
            {
                preferences.edit().putString("configuration", json).apply()
            }
            imageDownloader.init(response)
        }
    }

    private fun getConfigFromPreferences(): Observable<ConfigurationResponse>
    {
        val json = preferences.getString("configuration", "")
        val response: ConfigurationResponse? = Gson().fromJson(json, ConfigurationResponse::class.java)
        if (response == null)
        {
            return Observable.empty()
        }
        else
        {
            return Observable.just(response)
        }
    }
}