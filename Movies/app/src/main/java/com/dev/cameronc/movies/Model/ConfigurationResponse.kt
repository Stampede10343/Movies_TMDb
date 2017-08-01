package com.dev.cameronc.movies.Model


import com.google.gson.annotations.SerializedName


data class ConfigurationResponse(@SerializedName("images") var images: ImagSizes, @SerializedName(
        "change_keys") var changeKeys: List<String>)
