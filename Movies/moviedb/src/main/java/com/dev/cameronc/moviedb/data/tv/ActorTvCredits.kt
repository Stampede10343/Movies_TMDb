package com.dev.cameronc.moviedb.data.tv

import com.google.gson.annotations.SerializedName


data class ActorTvCredits(
        @SerializedName("cast")
        val tvCast: List<TvCast>,
        @SerializedName("crew")
        val crew: List<TvCrew>,
        @SerializedName("id")
        val id: Long
)

data class TvCast(
        @SerializedName("backdrop_path")
        val backdropPath: String,
        @SerializedName("character")
        val character: String,
        @SerializedName("credit_id")
        val creditId: String,
        @SerializedName("episode_count")
        val episodeCount: Int,
        @SerializedName("first_air_date")
        val firstAirDate: String,
        @SerializedName("genre_ids")
        val genreIds: List<Int>,
        @SerializedName("id")
        val id: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("origin_country")
        val originCountry: List<String>,
        @SerializedName("original_language")
        val originalLanguage: String,
        @SerializedName("original_name")
        val originalName: String,
        @SerializedName("overview")
        val overview: String,
        @SerializedName("popularity")
        val popularity: Double,
        @SerializedName("poster_path")
        val posterPath: String?,
        @SerializedName("vote_average")
        val voteAverage: Double,
        @SerializedName("vote_count")
        val voteCount: Int
)

data class TvCrew(
        @SerializedName("backdrop_path")
        val backdropPath: String,
        @SerializedName("credit_id")
        val creditId: String,
        @SerializedName("department")
        val department: String,
        @SerializedName("episode_count")
        val episodeCount: Int,
        @SerializedName("first_air_date")
        val firstAirDate: String,
        @SerializedName("genre_ids")
        val genreIds: List<Int>,
        @SerializedName("id")
        val id: Long,
        @SerializedName("job")
        val job: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("origin_country")
        val originCountry: List<Any>,
        @SerializedName("original_language")
        val originalLanguage: String,
        @SerializedName("original_name")
        val originalName: String,
        @SerializedName("overview")
        val overview: String,
        @SerializedName("popularity")
        val popularity: Double,
        @SerializedName("poster_path")
        val posterPath: String,
        @SerializedName("vote_average")
        val voteAverage: Double,
        @SerializedName("vote_count")
        val voteCount: Int
)