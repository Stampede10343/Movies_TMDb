package com.dev.cameronc.movies.model.tv

data class TvSeriesScreenModel(val id: Long, val name: String, val backdropPath: String?, val description: String?,
                               val firstAirDate: String, val lastAirDate: String, val genres: List<String>, val seasons: List<TvSeriesSeason>)