package com.dev.cameronc.movies.moviedetail

import android.view.View
import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.ViewModel
import com.dev.cameronc.movies.model.MovieRepo
import com.dev.cameronc.movies.model.movie.MovieDetails
import com.dev.cameronc.movies.model.movie.MovieReview
import io.reactivex.Observable
import io.reactivex.functions.Function4
import org.joda.time.DateTime
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(private val movieRepo: MovieRepo, private val movieRatingFinder: MovieRatingFinder) : ViewModel() {

    fun movieDetails(movieId: Long): Observable<ScreenState<MovieDetails>> {
        return Observable.zip(getMovieDetails(movieId), getMovieCredits(movieId), getRelatedMovies(movieId), getMovieReviews(movieId),
                Function4<MovieDetailsResponse, MovieCreditsResponse, SimilarMoviesResponse, List<MovieReview>, ScreenState<MovieDetails>>
                { movieDetails, credits, similarMovies, reviews ->
                    val movieYear = if (movieDetails.releaseDate.isNotEmpty()) movieDetails.releaseDate.subSequence(0, 4) else DateTime.now().year.toString()
                    val movieTitle = "${movieDetails.title} ($movieYear)"
                    val rating = movieRatingFinder.getUSRatingOrEmpty(movieDetails.releaseDates.releases)
                    val genreNames = movieDetails.genres.map { it.name }

                    ScreenState.Ready(MovieDetails(movieId, movieTitle, movieDetails.overview, movieDetails.releaseDate,
                            movieDetails.runtime, rating, if (rating.isNotEmpty()) View.VISIBLE else View.GONE,
                            movieDetails.backdropPath, genreNames, movieDetails.voteAverage.toString(),
                            if (movieDetails.voteAverage > 0.0) View.VISIBLE else View.GONE, credits.cast,
                            similarMovies.results, reviews))
                })
                .startWith(ScreenState.Loading())

    }

    private fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> =
            movieRepo.getMovieDetails(movieId)

    private fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> =
            movieRepo.getMovieCredits(movieId)

    private fun getRelatedMovies(movieId: Long): Observable<SimilarMoviesResponse> =
            movieRepo.getSimilarMovies(movieId)

    private fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> =
            movieRepo.getMovieReviews(movieId)

    fun movieImages(movieId: Long): Observable<List<String>> = movieRepo.getMovieImages(movieId)
}