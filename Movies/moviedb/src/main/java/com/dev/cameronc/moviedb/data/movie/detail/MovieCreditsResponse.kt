package com.dev.cameronc.moviedb.data.movie.detail

import com.google.gson.annotations.SerializedName

data class MovieCreditsResponse(
        @SerializedName("cast")
        val cast: List<Cast>,
        @SerializedName("crew")
        val crew: List<Crew>,
        @SerializedName("id")
        val id: Int
)

data class Cast(
        @SerializedName("cast_id")
        val castId: Long,
        @SerializedName("character")
        val character: String,
        @SerializedName("credit_id")
        val creditId: String,
        @SerializedName("gender")
        val gender: Int,
        @SerializedName("id")
        val id: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("order")
        val order: Int,
        @SerializedName("profile_path")
        val profilePath: String?
)

data class Crew(
        @SerializedName("credit_id")
        val creditId: String,
        @SerializedName("department")
        val department: String,
        @SerializedName("gender")
        val gender: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("job")
        val job: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("profile_path")
        val profilePath: String
)
