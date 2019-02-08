package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.moviedb.data.MultiSearchResponse
import com.dev.cameronc.moviedb.data.SearchResponse
import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.movie.MovieMapper
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.MovieVideo
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Observable
import org.joda.time.DateTime
import javax.inject.Inject

class MovieBoxDataStore @Inject constructor(boxStore: BoxStore, private val preferences: SharedPreferences, private val movieMapper: MovieMapper) : MovieDataSource {
    private val movieItemBox: Box<UpcomingMovie> = boxStore.boxFor(UpcomingMovie::class.java)

    override fun getUpcomingMovies(page: String): Observable<List<UpcomingMovie>> = Observable.just(movieItemBox.all)

    override fun saveMovies(movies: List<MovieResponseItem>) {
        val allMovies = movieItemBox.all
        val newMovies = movies
                .asSequence()
                .map { movieMapper.mapMovieResponseToUpcomingMovie(it) }
                .toMutableList()

        for (movie in allMovies) {
            newMovies.remove(movie)
        }
        preferences.edit().putLong("movie_save_time", DateTime.now().millis).apply()
        movieItemBox.put(newMovies)
    }

    override fun searchMovies(query: String): Observable<SearchResponse> = Observable.empty()

    override fun getMovieDetails(movieId: Long): Observable<MovieDetailsResponse> = Observable.empty()

    override fun getMovieCredits(movieId: Long): Observable<MovieCreditsResponse> = Observable.empty()

    override fun getSimilarMovies(movieId: Long): Observable<SimilarMoviesResponse> = Observable.empty()

    override fun searchAll(query: String): Observable<MultiSearchResponse> = Observable.empty()

    override fun getMovieReviews(movieId: Long): Observable<List<MovieReview>> = Observable.empty()

    override fun getVideosForMovie(movieId: Long): Observable<List<MovieVideo>> = Observable.empty()

    override fun getMovieImages(movieId: Long): Observable<List<String>> = Observable.empty()
}