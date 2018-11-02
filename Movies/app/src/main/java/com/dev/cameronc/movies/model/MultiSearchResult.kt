package com.dev.cameronc.movies.model

sealed class MultiSearchResult(val id: Long, val posterPath: String?) {
    data class MovieSearchResult(val movieId: Long, val moviePosterPath: String?, val title: String?) : MultiSearchResult(movieId, moviePosterPath)
    data class TelevisionSearchResult(val tvId: Long, val tvPosterPath: String?, val title: String?) : MultiSearchResult(tvId, tvPosterPath)
}