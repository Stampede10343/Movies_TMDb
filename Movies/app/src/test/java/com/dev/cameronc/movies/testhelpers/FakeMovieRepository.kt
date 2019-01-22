package com.dev.cameronc.movies.testhelpers

import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.ReleaseDates
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.MovieRepository
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.MovieVideo
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.reactivex.Observable

class FakeMovieRepository : MovieRepository {
    var upcomingMovies: Observable<List<UpcomingMovie>> = Observable.just(emptyList())
    var searchResponse: Observable<SearchResponse> = Observable.just(SearchResponse(0, 0, 0, emptyList()))
    var movieDetails: Observable<MovieDetailsResponse> = Observable.just(MovieDetailsResponse(false, "",
            false, 1, emptyList(), "", 1, "1", "en", "",
            "overview", 1.0, "", emptyList(), emptyList(), "2018-01-01", ReleaseDates(emptyList()),
            100, 90, emptyList(), "", "", "title", false, 1.0, 1))
    var movieCredits: Observable<MovieCreditsResponse> = Observable.just(MovieCreditsResponse(emptyList(), emptyList(), 1))
    var similarMovies: Observable<SimilarMoviesResponse> = Observable.just(SimilarMoviesResponse(0, emptyList(), 0, 0))
    var multiSearchResults: Observable<MultiSearchResponse> = Observable.just(MultiSearchResponse(0, emptyList(), 0, 0))
    var movieReviews: Observable<List<MovieReview>> = Observable.just(emptyList())
    var movieVideos: Observable<List<MovieVideo>> = Observable.just(emptyList())
    var movieImages: Observable<List<String>> = Observable.just(emptyList())

    override fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>> = upcomingMovies

    override fun saveMovies(movies: List<MovieResponseItem>) {
        // No Op
    }

    override fun searchMovies(query: String): Observable<SearchResponse> = searchResponse

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> = movieDetails

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> = movieCredits

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> = similarMovies

    override fun searchAll(query: String): Observable<MultiSearchResponse> = multiSearchResults

    override fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> = movieReviews

    override fun getVideosForMovie(movieId: Long): Observable<List<MovieVideo>> = movieVideos

    override fun getMovieImages(movieId: Long): Observable<List<String>> = movieImages
}