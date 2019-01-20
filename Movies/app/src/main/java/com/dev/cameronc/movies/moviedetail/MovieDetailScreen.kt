package com.dev.cameronc.movies.moviedetail

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.androidutilities.view.fadeAndSetGone
import com.dev.cameronc.androidutilities.view.fadeIn
import com.dev.cameronc.movies.*
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.model.movie.MovieDetails
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.start.StartScreen
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

class MovieDetailScreen : AppScreen, Bundleable {
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
    @Inject
    lateinit var galleryAdapter: GalleryAdapter

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

        movieDetailViewModel.movieDetails(movieId)
                .subscribe({ movieDetailsState ->
                    when (movieDetailsState) {
                        is ScreenState.Ready<MovieDetails> -> {
                            setupScreen(movieDetailsState.data, movieId)
                        }
                        is ScreenState.Loading<MovieDetails> -> {
                            showLoadingIndicator(true)
                        }
                    }
                },
                        { error -> Timber.e(error) }).disposeBy(this)

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

    private fun setupScreen(movieDetails: MovieDetails, movieId: Long) {
        movie_detail_scrollview.fadeIn()
        showLoadingIndicator(false)

        if (movieDetails.runtime > 0) {
            movie_detail_runtime.text = context.getString(R.string.movie_duration, movieDetails.runtime.toString())
            movie_detail_runtime.visibility = View.VISIBLE
        } else movie_detail_runtime.visibility = View.GONE

        movie_detail_rating
                .visibility = movieDetails.ratingVisibility
        movie_detail_rating.text = movieDetails.rating

        movie_detail_rating_average.visibility = movieDetails.voteAverageVisibility
        movie_detail_rating_average_image.visibility = movieDetails.voteAverageVisibility
        movie_detail_rating_average.text = movieDetails.voteAverage

        movie_detail_genre_list.visibility = View.VISIBLE
        movie_detail_genre_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        movie_detail_genre_list.adapter = MovieGenreAdapter(movieDetails.genres)

        imageDownloader.loadBackdrop(movieDetails.backdropPath, movie_detail_poster)
                .apply(RequestOptions.centerInsideTransform())
                .apply(RequestOptions().placeholder(R.color.dark_grey))
                .into(movie_detail_poster)
        movie_detail_title.text = movieDetails.title
        movie_detail_description.text = movieDetails.overview

        actorAdapter.setActors(movieDetails.cast)
        actorAdapter.onActorClicked {
            Navigator.getBackstack(context).goTo(ActorScreen.ActorScreenKey(it))
        }
        movie_detail_actors.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        movie_detail_actors.adapter = actorAdapter

        if (movieDetails.similarMovies.isEmpty()) {
            movie_detail_related_text.visibility = View.GONE
            movie_detail_related_movies.visibility = View.GONE

        } else {
            movie_detail_related_text.visibility = View.VISIBLE
            movie_detail_related_movies.visibility = View.VISIBLE
            movie_detail_related_movies.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            relatedMovieAdapter.relatedMovies = movieDetails.similarMovies
            relatedMovieAdapter.relatedMovieClickListener = { Navigator.getBackstack(context).goTo(MovieDetailKey(it)) }
            movie_detail_related_movies.adapter = relatedMovieAdapter
        }

        if (movieDetails.reviews.isEmpty()) {
            movie_detail_review_text.visibility = View.GONE
            movie_detail_reviews.visibility = View.GONE
        } else {
            movie_detail_review_text.visibility = View.VISIBLE
            movie_detail_reviews.visibility = View.VISIBLE
            movie_detail_reviews.layoutManager = LinearLayoutManager(context)
            movie_detail_reviews.adapter = reviewAdapter
            reviewAdapter.setReviews(movieDetails.reviews)
        }

        movie_detail_poster.setOnClickListener {
            movieDetailViewModel.movieImages(movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ imageUrls ->
                        if (gallery_pager_stub != null) {
                            gallery_pager_stub.inflate()
                        }
                        val pagerParent: View = findViewById(R.id.gallery_viewgroup)
                        val viewPager: ViewPager = pagerParent.findViewById(R.id.gallery_pager)
                        galleryAdapter.imageUrls.addAll(imageUrls)
                        galleryAdapter.imageClickListener = { pagerParent.fadeAndSetGone() }
                        viewPager.adapter = galleryAdapter
                        viewPager.offscreenPageLimit = 2
                        viewPager.setPageTransformer(false) { view, position ->
                            view.alpha = Math.max(1 - Math.abs(position), 0.2f)
                        }

                        pagerParent.fadeIn()
                        viewPager.bringToFront()
                    }, { error -> Timber.e(error) })
                    .disposeBy(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        movieDetailViewModel.onDestroy()
    }

    override fun getScreenName(): String = "Movie Detail"

    override fun handleBackPressed(): Boolean {
        val galleryParent = findViewById<View?>(R.id.gallery_viewgroup)
        return if (galleryParent != null && galleryParent.visibility == View.VISIBLE) {
            galleryParent.fadeAndSetGone()
            true
        } else super.handleBackPressed()
    }

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