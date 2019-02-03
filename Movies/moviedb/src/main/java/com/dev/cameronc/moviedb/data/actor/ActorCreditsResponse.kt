package com.dev.cameronc.moviedb.data.actor

import com.google.gson.annotations.SerializedName

data class ActorCreditsResponse(
        @SerializedName("cast")
        val cast: List<Role>,
        @SerializedName("crew")
        val crew: List<Role>,
        @SerializedName("id")
        val id: Long
)