package com.dev.cameronc.movies.model.movie

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class UpcomingMovie(@Id(assignable = true) var tmdbId: Long,
                         var posterPath: String?,
                         var adult: Boolean,
                         var overview: String,
                         var releaseDate: String,
                         var originalTitle: String,
                         var originalLanguage: String,
                         var title: String,
                         var backdropPath: String?,
                         var popularity: Float)