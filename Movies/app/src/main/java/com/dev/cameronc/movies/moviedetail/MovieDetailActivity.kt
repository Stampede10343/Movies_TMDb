package com.dev.cameronc.movies.moviedetail

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.moviedb.data.MovieResponseItem
import com.dev.cameronc.moviedb.data.ReleaseResult
import com.dev.cameronc.movies.BaseActivity
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.actor.ActorActivity
import com.dev.cameronc.movies.toDp
import io.objectbox.BoxStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_detail.*
import timber.log.Timber
import javax.inject.Inject

class MovieDetailActivity : BaseActivity() {
    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var objectBox: BoxStore
    @Inject
    lateinit var movieDetailViewModel: MovieDetailViewModel
    @Inject
    lateinit var actorAdapter: MovieActorAdapter
    @Inject
    lateinit var relatedMovieAdapter: RelatedMovieAdapter

    private lateinit var movie: MovieResponseItem
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieId = intent.getLongExtra(MOVIE, 0)
        movie = objectBox.boxFor(MovieResponseItem::class.java).get(movieId)

        subscriptions.add(movieDetailViewModel.getMovieDetails(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ detailResponse ->
                    TransitionManager.beginDelayedTransition(movie_detail_layout)
                    if (detailResponse.runtime > 0) {
                        movie_detail_runtime.text = getString(R.string.movie_duration, detailResponse.runtime.toString())
                        movie_detail_runtime.visibility = View.VISIBLE
                    } else movie_detail_runtime.visibility = View.GONE

                    val usRelease: ReleaseResult? = detailResponse.releaseDates.results.firstOrNull { it.iso31661 == "US" }
                    if (usRelease != null && usRelease.releaseDates.isNotEmpty() && usRelease.releaseDates.firstOrNull()?.certification?.isNotBlank() == true) {
                        movie_detail_rating.visibility = View.VISIBLE
                        movie_detail_rating.text = usRelease.releaseDates[0].certification
                    }
                    movie_detail_genre_list.visibility = View.VISIBLE
                    movie_detail_genre_list.adapter = MovieGenreAdapter(detailResponse.genres.asSequence().map { it.name }.toMutableList())
                    val margin = 4.toDp()
                    movie_detail_genre_list.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))

                }, { error ->
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                }))

        subscriptions.add(movieDetailViewModel.getMovieCredits(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ creditsResponse ->
                    actorAdapter.setActors(creditsResponse.cast)
                    actorAdapter.onActorClicked {
                        startActivity(ActorActivity.newIntent(this, it))
                    }
                    val margin = 8.toDp()
                    movie_detail_actors.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                    movie_detail_actors.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
                    movie_detail_actors.adapter = actorAdapter
                }, { error ->
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                }))

        subscriptions.add(movieDetailViewModel.getRelatedMovies(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ relatedResponse ->
                    val margin = 8.toDp()
                    movie_detail_related_movies.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                    movie_detail_related_movies.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
                    relatedMovieAdapter.relatedMovies = relatedResponse.results
                    movie_detail_related_movies.adapter = relatedMovieAdapter
                }, { error -> Timber.e(error) }))

        movie_detail_title.text = movie.title + ' ' + '(' + movie.releaseDate.subSequence(0, 4) + ')'
        imageDownloader.load(movie.posterPath, movie_detail_poster)
                .apply(RequestOptions.centerInsideTransform())
                .apply(RequestOptions().placeholder(R.color.dark_grey))
                .into(movie_detail_poster)
        movie_detail_genre_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        movie_detail_description.text = movie.overview

    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val MOVIE = "movie"

        fun newIntent(activity: Activity, movie: MovieResponseItem): Intent {
            val intent = Intent(activity, MovieDetailActivity::class.java)
            intent.putExtra(MOVIE, movie.uniqueId)

            return intent
        }
    }
}
