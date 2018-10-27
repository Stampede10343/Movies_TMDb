package com.dev.cameronc.moviedb.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class UpcomingMovieResponse(@SerializedName("page") var page: Int, @SerializedName(
        "results") var results: MutableList<MovieResponseItem>, @SerializedName("dates") var dates: DateRange, @SerializedName(
        "total_pages") @Expose var totalPages: Int, @SerializedName("total_results") var totalResults: Int)

data class DateRange(@SerializedName("maximum") var maximum: String, @SerializedName("minimum") var minimum: String)
