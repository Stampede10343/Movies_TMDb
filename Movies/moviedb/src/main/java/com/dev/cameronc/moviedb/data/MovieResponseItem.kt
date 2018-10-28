package com.dev.cameronc.moviedb.data

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class MovieResponseItem(@SerializedName("poster_path") var posterPath: String?, @SerializedName("adult") var adult: Boolean,
                             @SerializedName("overview") var overview: String, @SerializedName("release_date") var releaseDate: String,
                             @SerializedName("id") @Id(assignable = true) var tmdbId: Long, var uniqueId: Long, @SerializedName("original_title") var originalTitle: String,
                             @SerializedName("original_language") var originalLanguage: String, @SerializedName("title") var title: String,
                             @SerializedName("backdrop_path") var backdropPath: String?, @SerializedName("popularity") var popularity: Float,
                             @SerializedName("vote_count") var voteCount: Int, @SerializedName("video") var video: Boolean,
                             @SerializedName("vote_average") var voteAverage: Float)