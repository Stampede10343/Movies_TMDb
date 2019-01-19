package com.dev.cameronc.movies.model

import com.dev.cameronc.moviedb.data.tv.TvSeriesDetails
import io.reactivex.Observable

interface TvRepository {
    fun getTvDetails(seriesId: Long): Observable<TvSeriesDetails>
}