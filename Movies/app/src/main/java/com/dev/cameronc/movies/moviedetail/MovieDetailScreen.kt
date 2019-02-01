package com.dev.cameronc.movies.moviedetail

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import com.dev.cameronc.androidutilities.BaseKey
import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.movies.AppScreen
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.movie.MovieDetails
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.moviedetail.groupie.*
import com.dev.cameronc.movies.start.StartScreen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import io.objectbox.BoxStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.gallery_view.view.*
import kotlinx.android.synthetic.main.movie_detail_screen.view.*
import timber.log.Timber
import javax.inject.Inject

class MovieDetailScreen : AppScreen, Bundleable {
    @Inject
    lateinit var movieDetailViewModel: MovieDetailViewModel
    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var objectBox: BoxStore
    @Inject
    lateinit var galleryAdapter: GalleryAdapter
    private lateinit var movieImageViewerController: MovieImageViewerController

    private var movie: UpcomingMovie? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        if (!isInEditMode) MoviesApp.activityComponent.inject(this)
    }

    override fun viewReady() {
        movie_detail_toolbar.setNavigationOnClickListener {
            val backstack = Navigator.getBackstack(context)
            if (backstack.getHistory<BaseKey>().size > 2) {
                backstack.goUp(StartScreen.StartKey())
            } else backstack.goBack()
        }

        val movieId = Backstack.getKey<MovieDetailKey>(context).tmdbId
        movie = objectBox.boxFor(UpcomingMovie::class.java).get(movieId)
        movieImageViewerController = MovieImageViewerController(gallery_pager)

        movieDetailViewModel.movieDetails(movieId)
                .subscribe({ movieDetailsState ->
                    when (movieDetailsState) {
                        is ScreenState.Ready<MovieDetails> -> {
                            showLoadingIndicator(false)

                            GroupAdapter<ViewHolder>().apply {
                                val data: MovieDetails = movieDetailsState.data
                                add(MovieDetailBackdrop(data.backdropPath, imageDownloader).apply {
                                    backdropClicked {
                                        showMovieGallery(movieDetailsState.data.id)
                                    }
                                })
                                val runtime = context.getString(R.string.movie_duration, data.runtime.toString())
                                add(MovieDetailInfo(data.title, data.rating, runtime, data.voteAverage, data.genres, data.overview))
                                add(MovieDetailCast(data.cast, imageDownloader))
                                add(MovieReviews(data.reviews))
                                add(RelatedMoviesItem(data.similarMovies, imageDownloader))

                                movie_detail_recyclerview.adapter = this
                                movie_detail_recyclerview.layoutManager = LinearLayoutManager(context)
                            }
                        }
                        is ScreenState.Loading<MovieDetails> -> {
                            showLoadingIndicator(true)
                        }
                    }
                },
                        { error -> Timber.e(error) }).disposeBy(this)
    }

    private fun showMovieGallery(movieId: Long) {
        movieDetailViewModel.movieImages(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ imageUrls ->
                    movieImageViewerController.show(imageUrls, galleryAdapter)
                }, { error -> Timber.e(error) })
                .disposeBy(this)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            movieImageViewerController.restoreState(state)
            super.onRestoreInstanceState(state.getParcelable("super"))
        } else super.onRestoreInstanceState(state)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable("super", super.onSaveInstanceState())
            movieImageViewerController.saveState(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        movieDetailViewModel.onDestroy()
    }

    override fun getScreenName(): String = "Movie Detail"

    override fun handleBackPressed(): Boolean {
        return if (gallery_pager.visibility == View.VISIBLE) {
            movieImageViewerController.hide()
            true
        } else super.handleBackPressed()
    }

    @Parcelize
    data class MovieDetailKey(val tmdbId: Long) : StateKey, Parcelable {
        override fun layout(): Int = R.layout.movie_detail_screen
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}