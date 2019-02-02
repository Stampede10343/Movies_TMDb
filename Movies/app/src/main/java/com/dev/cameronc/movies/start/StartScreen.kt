package com.dev.cameronc.movies.start

import android.content.Context
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.AttributeSet
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import com.dev.cameronc.androidutilities.KeyboardHelper
import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.movies.AppScreen
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.options.OptionsScreen
import com.dev.cameronc.movies.search.SearchResultsScreen
import com.dev.cameronc.movies.tv.TvSeriesScreen
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.start_screen.view.*
import timber.log.Timber
import javax.inject.Inject


class StartScreen : AppScreen, MovieCardAdapter.MovieAdapterListener, Bundleable, SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    @Inject
    lateinit var viewModel: StartViewModel
    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var searchResultsAdapter: SearchResultAdapter
    @Inject
    lateinit var keyboardHelper: KeyboardHelper

    private lateinit var moviesAdapter: MovieCardAdapter
    private val searchResultsRecyclerView: RecyclerView = RecyclerView(context).apply { setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey)) }
    private lateinit var searchResultsWindow: PopupWindow

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        if (!isInEditMode) MoviesApp.activityComponent.inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return

        start_toolbar.navigationIcon = null
        searchResultsWindow = PopupWindow(searchResultsRecyclerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        start_toolbar.menu.add(R.string.options).setOnMenuItemClickListener(this)

        keyboardHelper.listenForKeyboard()
        keyboardHelper.keyboardOpened = { setWindowSizeToContentSize() }
        keyboardHelper.keyboardClosed = { searchResultsWindow.dismiss() }

        movie_search_view.setOnQueryTextListener(this)
        searchResultsRecyclerView.adapter = searchResultsAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setupSearchResultClickListener()

        viewModel.searchResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ results ->
                    searchResultsAdapter.setSearchResults(results)
                }, { error -> Timber.e(error) })
                .disposeBy(this)

        moviesAdapter = MovieCardAdapter(imageDownloader, emptyList<UpcomingMovie>().toMutableList(), this)
        start_movies.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.grid_columns))
        start_movies.adapter = moviesAdapter

        viewModel.getUpcomingMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ screenState ->
                    when (screenState) {
                        is ScreenState.Ready -> {
                            showLoadingIndicator(false)
                            moviesAdapter.addMovies(screenState.data)
                            if (viewState != null) restoreHierarchyState(viewState)
                            viewState = null
                        }

                        is ScreenState.Loading -> showLoadingIndicator(true)
                    }
                }, { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() })
                .disposeBy(this)
    }

    private fun setupSearchResultClickListener() {
        searchResultsAdapter.resultClickListener = {
            viewModel.onSearchEntered("")

            keyboardHelper.dismissKeyboard()
            keyboardHelper.clearListener()

            searchResultsWindow.dismiss()

            when (it.type) {
                MediaType.Movie -> navigator.goToScreen(MovieDetailScreen.MovieDetailKey(it.id))
                MediaType.Person -> navigator.goToScreen(ActorScreen.ActorScreenKey(it.id))
                MediaType.Television -> navigator.goToScreen(TvSeriesScreen.Key(it.id))
            }

        }
    }

    private fun setWindowSizeToContentSize() {
        searchResultsWindow.height = start_movies.height
    }

    override fun getScreenName(): String = "Start Screen"

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (searchResultsWindow.isShowing) searchResultsWindow.dismiss()
        keyboardHelper.dismissKeyboard()
        keyboardHelper.clearListener()
        viewModel.onDestroy()
    }

    override fun loadMore() {
        viewModel.loadMoreMovies()
    }

    override fun onItemClicked(tmdbId: Long) {
        Navigator.getBackstack(context).goTo(MovieDetailScreen.MovieDetailKey(tmdbId))
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        keyboardHelper.dismissKeyboard()
        if (!query.isNullOrBlank()) Navigator.getBackstack(context).goTo(SearchResultsScreen.Key(query))
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (height > 0 && !searchResultsWindow.isShowing) searchResultsWindow.showAsDropDown(start_toolbar)
        viewModel.queryTextChanged(query)

        if (query.isNullOrBlank()) searchResultsWindow.dismiss()
        return true
    }

    override fun handleBackPressed(): Boolean {
        if (searchResultsWindow.isShowing) searchResultsWindow.dismiss() else return false

        keyboardHelper.dismissKeyboard()

        return true
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.title == "Options") {
            post { Navigator.getBackstack(context).goTo(OptionsScreen.Key()) }
        }

        return false
    }

    @Parcelize
    class StartKey : StateKey, Parcelable {
        override fun layout(): Int = R.layout.start_screen
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}