package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.data.tv.TvSeriesDetails
import io.objectbox.BoxStore
import io.reactivex.Observable
import javax.inject.Inject

class ObjectBoxTvDataStore @Inject constructor(private val objectBox: BoxStore) : TvDataStore {
    override fun getTvSeriesData(seriesId: Long): Observable<TvSeriesDetails> {
        return Observable.empty()//just(objectBox.boxFor(TvSeriesDetails::class.java).get(seriesId))
    }

    override fun saveTvSeriesData(seriesDetails: TvSeriesDetails) {
        //objectBox.boxFor(TvSeriesDetails::class.java).put(seriesDetails)
    }
}