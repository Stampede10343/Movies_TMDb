package com.dev.cameronc.movies.model.movie

import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.ReviewResult
import javax.inject.Inject

class MovieMapper @Inject constructor() {

    fun mapMovieResponseToUpcomingMovie(movieResponseItem: MovieResponseItem): UpcomingMovie {
        return UpcomingMovie(movieResponseItem.tmdbId, movieResponseItem.posterPath, movieResponseItem.adult,
                movieResponseItem.overview, movieResponseItem.releaseDate, movieResponseItem.originalTitle,
                movieResponseItem.originalLanguage, movieResponseItem.title, movieResponseItem.backdropPath,
                movieResponseItem.popularity)
    }

    fun mapMovieReviewResponseToMovieReview(responseReview: ReviewResult): MovieReview =
            MovieReview(responseReview.id, responseReview.author, responseReview.content.trim())
}