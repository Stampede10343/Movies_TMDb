package com.dev.cameronc.movies.tv

import com.dev.cameronc.movies.ViewModel
import com.dev.cameronc.movies.model.TvRepository
import com.dev.cameronc.movies.model.tv.TvSeriesScreenModel
import com.dev.cameronc.movies.model.tv.TvSeriesSeason
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TvSeriesViewModel @Inject constructor(private val tvRepository: TvRepository) : ViewModel() {

    fun getSeriesInfo(seriesId: Long): Observable<TvSeriesScreenModel> {
        return tvRepository.getTvDetails(seriesId)
                .map { seriesDetails ->
                    TvSeriesScreenModel(seriesDetails.id, seriesDetails.name, seriesDetails.backdropPath, seriesDetails.overview, seriesDetails.firstAirDate,
                            seriesDetails.lastAirDate, seriesDetails.genres.map { it.name }, seriesDetails.seasons.map { TvSeriesSeason(it.posterPath, it.name) })
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}