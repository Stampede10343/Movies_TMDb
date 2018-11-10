package com.dev.cameronc.movies.moviedetail

import android.arch.lifecycle.ViewModel
import android.view.View
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieRatingFinder
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.MovieRepo
import com.dev.cameronc.movies.model.movie.MovieDetails
import com.dev.cameronc.movies.model.movie.MovieReview
import io.reactivex.Observable
import io.reactivex.functions.Function4
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(private val movieRepo: MovieRepo, private val movieRatingFinder: MovieRatingFinder) : ViewModel() {

    fun movieDetails(movieId: Long): Observable<MovieDetails> {
        return Observable.zip(getMovieDetails(movieId), getMovieCredits(movieId), getRelatedMovies(movieId), getMovieReviews(movieId),
                Function4<MovieDetailsResponse, MovieCreditsResponse, SimilarMoviesResponse, List<MovieReview>, MovieDetails>
                { movieDetails, credits, similarMovies, reviews ->
                    val movieTitle = movieDetails.title + ' ' + '(' + movieDetails.releaseDate.subSequence(0, 4) + ')'
                    val rating = movieRatingFinder.getUSRatingOrEmpty(movieDetails.releaseDates.releases)
                    val genreNames = movieDetails.genres.map { it.name }

                    MovieDetails(movieTitle, movieDetails.overview, movieDetails.releaseDate,
                            movieDetails.runtime, rating, if (rating.isNotEmpty()) View.VISIBLE else View.GONE,
                            movieDetails.backdropPath, genreNames, movieDetails.voteAverage.toString(),
                            if (movieDetails.voteAverage > 0.0) View.VISIBLE else View.GONE, credits.cast,
                            similarMovies.results, reviews)
                })

    }

    private fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> =
            movieRepo.getMovieDetails(movieId)

    private fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> =
            movieRepo.getMovieCredits(movieId)

    private fun getRelatedMovies(movieId: Long): Observable<SimilarMoviesResponse> =
            movieRepo.getSimilarMovies(movieId)

    private fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> =
            movieRepo.getMovieReviews(movieId)
}