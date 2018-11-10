package com.dev.cameronc.moviedb.data.movie.detail
import com.google.gson.annotations.SerializedName

data class SimilarMoviesResponse(
        @SerializedName("page")
    val page: Int,
        @SerializedName("results")
    val results: List<SimilarMovie>,
        @SerializedName("total_pages")
    val totalPages: Int,
        @SerializedName("total_results")
    val totalResults: Int
)

data class SimilarMovie(
        @SerializedName("vote_count") val voteCount: Int,
        @SerializedName("id") val id: Long,
        @SerializedName("video") val video: Boolean,
        @SerializedName("vote_average") val voteAverage: Float,
        @SerializedName("title") val title: String,
        @SerializedName("popularity") val popularity: Double,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("original_language") val originalLanguage: String,
        @SerializedName("original_title") val originalTitle: String,
        @SerializedName("genre_ids") val genreIds: List<Int>,
        @SerializedName("backdrop_path") val backdropPath: String?,
        @SerializedName("adult") val adult: Boolean,
        @SerializedName("overview") val overview: String,
        @SerializedName("release_date") val releaseDate: String
)