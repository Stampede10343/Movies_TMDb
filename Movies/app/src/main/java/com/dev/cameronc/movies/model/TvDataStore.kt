package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.data.tv.TvSeriesDetails
import io.reactivex.Observable

interface TvDataStore {
    fun getTvSeriesData(seriesId: Long): Observable<TvSeriesDetails>
    fun saveTvSeriesData(seriesDetails: TvSeriesDetails)
}
