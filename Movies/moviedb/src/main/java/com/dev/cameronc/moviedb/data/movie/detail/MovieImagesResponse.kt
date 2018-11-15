package com.dev.cameronc.moviedb.data.movie.detail

import com.google.gson.annotations.SerializedName


data class MovieImagesResponse(
        @SerializedName("backdrops")
        val backdrops: List<Backdrop>,
        @SerializedName("id")
        val id: Int,
        @SerializedName("posters")
        val posters: List<Poster>
)

data class Backdrop(
        @SerializedName("aspect_ratio")
        val aspectRatio: Double,
        @SerializedName("file_path")
        val filePath: String,
        @SerializedName("height")
        val height: Int,
        @SerializedName("iso_639_1")
        val iso6391: Any?,
        @SerializedName("vote_average")
        val voteAverage: Double,
        @SerializedName("vote_count")
        val voteCount: Int,
        @SerializedName("width")
        val width: Int
)

data class Poster(
        @SerializedName("aspect_ratio")
        val aspectRatio: Double,
        @SerializedName("file_path")
        val filePath: String,
        @SerializedName("height")
        val height: Int,
        @SerializedName("iso_639_1")
        val iso6391: String,
        @SerializedName("vote_average")
        val voteAverage: Double,
        @SerializedName("vote_count")
        val voteCount: Int,
        @SerializedName("width")
        val width: Int
)