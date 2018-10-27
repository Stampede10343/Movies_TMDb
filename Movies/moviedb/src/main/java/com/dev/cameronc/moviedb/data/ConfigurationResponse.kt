package com.dev.cameronc.moviedb.data


import com.google.gson.annotations.SerializedName


data class ConfigurationResponse(@SerializedName("images") var images: ImageSizes, @SerializedName(
        "change_keys") var changeKeys: List<String>)
