package com.dev.cameronc.movies.MovieDetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.dev.cameronc.movies.BaseActivity
import com.dev.cameronc.movies.Model.MovieResponseItem
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import kotlinx.android.synthetic.main.activity_movie_detail.*
import javax.inject.Inject

class MovieDetailActivity : BaseActivity()
{
    private lateinit var movie: MovieResponseItem
    @Inject
    lateinit var imageDownloader: MovieImageDownloader

    override fun onCreate(savedInstanceState: Bundle?)
    {
        getApp().getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        movie = intent.getParcelableExtra(MOVIE)

        movie_detail_title.text = movie.title + ' ' + '(' + movie.releaseDate.subSequence(0, 4) + ')'
        imageDownloader.load(movie.posterPath, movie_detail_poster)?.into(movie_detail_poster)
        movie_detail_genre_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        movie_detail_genre_list.adapter = MovieGenreAdapter(movie.genreTags!!)
        movie_detail_description.text = movie.overview

    }

    companion object
    {
        const val MOVIE = "movie"

        fun newIntent(activity: Activity, movie: MovieResponseItem): Intent
        {
            val intent = Intent(activity, MovieDetailActivity::class.java)
            intent.putExtra(MOVIE, movie)

            return intent
        }
    }
}
