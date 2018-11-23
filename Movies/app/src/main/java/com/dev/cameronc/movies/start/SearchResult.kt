package com.dev.cameronc.movies.start

import com.dev.cameronc.androidutilities.Identifiable

data class SearchResult(override val id: Long, val imagePath: String?, val title: String, val type: MediaType) : Identifiable

enum class MediaType {
    Movie,
    Person,
    Television
}