package com.dev.cameronc.movies.Model

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class MovieRepo @Inject constructor(private val movieDbApi: MovieDbApi/*, private val movieDao: MovieDao*/) : MovieRepositoy
{
    private var genres: MutableMap<Int, String>? = null

    override fun getUpcomingMovies(page: String): Observable<List<MovieResponseItem>>
    {
        return Observable.concat<List<MovieResponseItem>>(getMoviesFromApi(page), getMoviesFromDao(page)).filter { it.isNotEmpty() }.first(
                emptyList()).toObservable()
    }

    private fun getMoviesFromDao(
            page: String): Observable<MutableList<MovieResponseItem>> = Observable.never() //movieDao.getMovies().subscribeOn(Schedulers.io()).toObservable()

    private fun getMoviesFromApi(page: String): Observable<List<MovieResponseItem>>
    {
        val upcomingMovies: Observable<UpcomingMovieResponse> = movieDbApi.getUpcomingMovies(page).subscribeOn(Schedulers.io())

        return Observable.zip<UpcomingMovieResponse, Map<Int, String>, List<MovieResponseItem>>(upcomingMovies, genreMapper(), BiFunction { response, genres ->
            addGenres(response.results, genres)
            response.results
        })
    }

    private fun addGenres(results: List<MovieResponseItem>, genres: Map<Int, String>)
    {
        results.forEach { mov -> mov.genreIds.forEach { mov.addGenre(genres[it]!!) } }
    }

    private fun genreMapper(): Observable<Map<Int, String>>
    {
        if (genres == null)
        {
            genres = HashMap()
            return movieDbApi.getGenres().map { it.genres.forEach { addGenreIntoMap(it) } }.map { genres }
        }
        else
        {
            return Observable.just(genres)
        }
    }

    private fun addGenreIntoMap(genre: Genre)
    {
        genres?.put(genre.id, genre.name)
    }

    override fun saveMovies(movies: List<MovieResponseItem>)
    {
        //movieDao.saveMovies(movies.toMutableList())
    }

    override fun getConfiguration(): ObservableSource<out ConfigurationResponse>
    {
        return movieDbApi.getConfiguration()
    }
}