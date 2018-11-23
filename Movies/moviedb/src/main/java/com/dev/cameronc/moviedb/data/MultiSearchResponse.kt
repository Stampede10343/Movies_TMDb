package com.dev.cameronc.moviedb.data

import com.google.gson.annotations.SerializedName

class MultiSearchResponse(
        @SerializedName("page")
        val page: Int,
        @SerializedName("results")
        val results: List<MultiSearchResult>,
        @SerializedName("total_pages")
        val totalPages: Int,
        @SerializedName("total_results")
        val totalResults: Int
)

data class MultiSearchResult(
        @SerializedName("adult")
        val adult: Boolean,
        @SerializedName("backdrop_path")
        val backdropPath: Any?,
        @SerializedName("first_air_date")
        val firstAirDate: String,
        @SerializedName("genre_ids")
        val genreIds: List<Long>,
        @SerializedName("id")
        val id: Long,
        @SerializedName("known_for")
        val knownFor: List<KnownFor>,
        @SerializedName("media_type")
        val mediaType: String,
        @SerializedName("name")
        val name: String?,
        @SerializedName("origin_country")
        val originCountry: List<Any>,
        @SerializedName("original_language")
        val originalLanguage: String,
        @SerializedName("original_name")
        val originalName: String,
        @SerializedName("original_title")
        val originalTitle: String,
        @SerializedName("overview")
        val overview: String,
        @SerializedName("popularity")
        val popularity: Double,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("profile_path")
        val profilePath: String,
        @SerializedName("release_date")
        val releaseDate: String,
        @SerializedName("title")
        val title: String?,
        @SerializedName("video")
        val video: Boolean,
        @SerializedName("vote_average")
        val voteAverage: Float,
        @SerializedName("vote_count")
        val voteCount: Int
)

data class KnownFor(
        @SerializedName("adult")
        val adult: Boolean,
        @SerializedName("backdrop_path")
        val backdropPath: String,
        @SerializedName("genre_ids")
        val genreIds: List<Int>,
        @SerializedName("id")
        val id: Long,
        @SerializedName("media_type")
        val mediaType: String,
        @SerializedName("original_language")
        val originalLanguage: String,
        @SerializedName("original_title")
        val originalTitle: String,
        @SerializedName("overview")
        val overview: String,
        @SerializedName("popularity")
        val popularity: Double,
        @SerializedName("poster_path")
        val posterPath: String,
        @SerializedName("release_date")
        val releaseDate: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("video")
        val video: Boolean,
        @SerializedName("vote_average")
        val voteAverage: Double,
        @SerializedName("vote_count")
        val voteCount: Int
)
