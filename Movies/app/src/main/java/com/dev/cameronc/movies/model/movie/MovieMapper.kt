package com.dev.cameronc.movies.model.movie

import com.dev.cameronc.moviedb.data.MovieResponseItem
import javax.inject.Inject

class MovieMapper @Inject constructor() {

    fun mapMovieResponseToUpcomingMovie(movieResponseItem: MovieResponseItem): UpcomingMovie {
        return UpcomingMovie(movieResponseItem.tmdbId, movieResponseItem.posterPath, movieResponseItem.adult,
                movieResponseItem.overview, movieResponseItem.releaseDate, movieResponseItem.originalTitle,
                movieResponseItem.originalLanguage, movieResponseItem.title, movieResponseItem.backdropPath,
                movieResponseItem.popularity)
    }
}