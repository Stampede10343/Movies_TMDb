package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.tv.TvSeriesDetails
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppTvRepository @Inject constructor(private val movieDbApi: MovieDbApi, private val tvOfflineDataStore: TvDataStore) : TvRepository {

    override fun getTvDetails(seriesId: Long): Observable<TvSeriesDetails> {
        val tvSeriesDetails = movieDbApi.tvSeriesDetails(seriesId)
                .doOnNext { tvOfflineDataStore.saveTvSeriesData(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        return Observable.concat(tvOfflineDataStore.getTvSeriesData(seriesId), tvSeriesDetails)
    }
}