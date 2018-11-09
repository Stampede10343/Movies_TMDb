package com.dev.cameronc.movies.moviedetail

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.moviedb.data.ReleaseResult
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.start.StartScreen
import com.dev.cameronc.movies.toDp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.statebundle.StateBundle
import io.objectbox.BoxStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.movie_detail_screen.view.*
import timber.log.Timber
import javax.inject.Inject

class MovieDetailScreen : BaseScreen, Bundleable {
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
    @Inject
    lateinit var reviewAdapter: MovieReviewAdapter

    private var movie: UpcomingMovie? = null
    private var previousScrollY: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        if (!isInEditMode) MoviesApp.activityComponent.inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return

        movie_detail_toolbar.setNavigationOnClickListener { Navigator.getBackstack(context).goUp(StartScreen.StartKey()) }

        val movieId = Backstack.getKey<MovieDetailKey>(context).tmdbId
        movie = objectBox.boxFor(UpcomingMovie::class.java).get(movieId)

        setListMargins()

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

                    imageDownloader.loadBackdrop(detailResponse.backdropPath, movie_detail_poster)
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
                    movie_detail_actors.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
                    movie_detail_actors.adapter = actorAdapter
                }, { error ->
                    Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                }).disposeBy(this)

        movieDetailViewModel.getRelatedMovies(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ relatedResponse ->
                    movie_detail_related_movies.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
                    relatedMovieAdapter.relatedMovies = relatedResponse.results
                    relatedMovieAdapter.relatedMovieClickListener = { Navigator.getBackstack(context).goTo(MovieDetailKey(it)) }
                    movie_detail_related_movies.adapter = relatedMovieAdapter

                    postDelayed({
                        restoreHierarchyState(viewState)
                    }, 100)
                }, { error -> Timber.e(error) }).disposeBy(this)

        movieDetailViewModel.getMovieReviews(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ reviews ->
                    movie_detail_ratings.layoutManager = LinearLayoutManager(context)
                    movie_detail_ratings.adapter = reviewAdapter
                    reviewAdapter.setReviews(reviews)
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

    private fun setListMargins() {
        val genreMargin = 4.toDp()
        (movie_detail_genre_list as RecyclerView).addItemDecoration(MarginItemDecoration(Rect(genreMargin, genreMargin, genreMargin, genreMargin)))
        val posterMargin = 8.toDp()
        movie_detail_actors.addItemDecoration(MarginItemDecoration(Rect(posterMargin, posterMargin, posterMargin, posterMargin)))
        movie_detail_related_movies.addItemDecoration(MarginItemDecoration(
                Rect(posterMargin, posterMargin, posterMargin, posterMargin)))
    }

    override fun getScreenName(): String = "Movie Detail"

    override fun toBundle(): StateBundle = super.toBundle().apply { putInt("scrollY", movie_detail_scrollview.scrollY) }

    override fun fromBundle(bundle: StateBundle?) {
        super.fromBundle(bundle)
        previousScrollY = bundle?.getInt("scrollY") ?: 0
    }

    @Parcelize
    data class MovieDetailKey(val tmdbId: Long) : StateKey, Parcelable {
        override fun layout(): Int = R.layout.movie_detail_screen
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}