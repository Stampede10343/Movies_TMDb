package com.dev.cameronc.movies.start

import android.content.Context
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.Toast
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.appComponent
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class StartScreen : BaseScreen, MovieCardAdapter.MovieAdapterListener, Bundleable {
    @Inject
    lateinit var viewModel: StartViewModel
    @Inject
    lateinit var imageDownloader: MovieImageDownloader

    private lateinit var moviesAdapter: MovieCardAdapter
    private lateinit var moviesList: RecyclerView
    private var listState: Parcelable? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        if (!isInEditMode) context.appComponent().inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return

        moviesList = findViewById(R.id.start_movies)
        moviesAdapter = MovieCardAdapter(imageDownloader, emptyList<UpcomingMovie>().toMutableList(), this)
        moviesList.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.grid_columns))
        moviesList.adapter = moviesAdapter

        viewModel.getUpcomingMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { movies ->
                            moviesAdapter.addMovies(movies)
                            moviesList.layoutManager?.onRestoreInstanceState(listState)
                        },
                        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() })
                .disposeBy(this)
    }

    override fun loadMore() {
        viewModel.loadMoreMovies()
    }

    override fun onItemClicked(tmdbId: Long) {
//        val movieDetailIntent = MovieDetailActivity.newIntent(context as Activity, tmdbId)
        //startActivity(movieDetailIntent)
        Navigator.getBackstack(context).goTo(MovieDetailScreen.MovieDetailKey(tmdbId))
    }

    override fun fromBundle(bundle: StateBundle?) {
        listState = bundle?.getParcelable("scrollposition")
    }

    override fun toBundle(): StateBundle {
        val stateBundle = StateBundle()
        stateBundle.putParcelable("scrollposition", (moviesList.layoutManager as? LinearLayoutManager)?.onSaveInstanceState())
        return stateBundle
    }

    @Parcelize
    class StartKey : StateKey, Parcelable {
        override fun layout(): Int = R.layout.activity_start
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}