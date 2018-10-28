package com.dev.cameronc.movies.moviedetail

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.moviedb.data.ReleaseResult
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.appComponent
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.toDp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import io.objectbox.BoxStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_movie_detail.view.*
import timber.log.Timber
import javax.inject.Inject

class MovieDetailScreen : BaseScreen {
    @Inject
    lateinit var movieDetailViewModel: MovieDetailViewModel
    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var objectBox: BoxStore
    @Inject
    lateinit var actorAdapter: MovieActorAdapter
    @Inject
    lateinit var relatedMovieAdapter: RelatedMovieAdapter

    private var movie: UpcomingMovie? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        if (!isInEditMode) context.appComponent().inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return
        val movieId = Backstack.getKey<MovieDetailKey>(context).tmdbId
        movie = objectBox.boxFor(UpcomingMovie::class.java).get(movieId)

        movieDetailViewModel.getMovieDetails(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ detailResponse ->
                    TransitionManager.beginDelayedTransition(movie_detail_layout)
                    if (detailResponse.runtime > 0) {
                        movie_detail_runtime.text = context.getString(R.string.movie_duration, detailResponse.runtime.toString())
                        movie_detail_runtime.visibility = View.VISIBLE
                    } else movie_detail_runtime.visibility = View.GONE

                    val usRelease: ReleaseResult? = detailResponse.releaseDates.results.firstOrNull { it.iso31661 == "US" }
                    if (usRelease != null && usRelease.releaseDates.isNotEmpty() && usRelease.releaseDates.firstOrNull()?.certification?.isNotBlank() == true) {
                        movie_detail_rating.visibility = View.VISIBLE
                        movie_detail_rating.text = usRelease.releaseDates[0].certification
                    }
                    movie_detail_genre_list.visibility = View.VISIBLE
                    movie_detail_genre_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    movie_detail_genre_list.adapter = MovieGenreAdapter(detailResponse.genres.asSequence().map { it.name }.toMutableList())
                    val margin = 4.toDp()
                    movie_detail_genre_list.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))

                    imageDownloader.load(detailResponse.posterPath, movie_detail_poster)
                            .apply(RequestOptions.centerCropTransform())
                            .apply(RequestOptions().placeholder(R.color.dark_grey))
                            .into(movie_detail_poster)
                    movie_detail_title.text = detailResponse.title + ' ' + '(' + detailResponse.releaseDate.subSequence(0, 4) + ')'
                    movie_detail_description.text = detailResponse.overview
                }, { error ->
                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                }).disposeBy(this)

        movieDetailViewModel.getMovieCredits(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ creditsResponse ->
                    actorAdapter.setActors(creditsResponse.cast)
                    actorAdapter.onActorClicked {
                        Navigator.getBackstack(context).goTo(ActorScreen.ActorScreenKey(it))
                    }
                    val margin = 8.toDp()
                    movie_detail_actors.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                    movie_detail_actors.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
                    movie_detail_actors.adapter = actorAdapter
                }, { error ->
                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                }).disposeBy(this)

        movieDetailViewModel.getRelatedMovies(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ relatedResponse ->
                    val margin = 8.toDp()
                    movie_detail_related_movies.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                    movie_detail_related_movies.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
                    relatedMovieAdapter.relatedMovies = relatedResponse.results
                    relatedMovieAdapter.relatedMovieClickListener = { Navigator.getBackstack(context).goTo(MovieDetailKey(it)) }
                    movie_detail_related_movies.adapter = relatedMovieAdapter
                }, { error -> Timber.e(error) }).disposeBy(this)

        if (movie != null) {
            movie_detail_title.text = movie!!.title + ' ' + '(' + movie!!.releaseDate.subSequence(0, 4) + ')'
            imageDownloader.load(movie!!.posterPath, movie_detail_poster)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions().placeholder(R.color.dark_grey))
                    .into(movie_detail_poster)
            movie_detail_genre_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            movie_detail_description.text = movie!!.overview
        }
    }

    @Parcelize
    data class MovieDetailKey(val tmdbId: Long) : StateKey, Parcelable {
        override fun layout(): Int = R.layout.activity_movie_detail
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}