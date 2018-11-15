package com.dev.cameronc.movies.model.movie

import com.dev.cameronc.moviedb.data.movie.detail.video.Site

data class MovieVideo(val id: String, val site: Site, val videoKey: String)