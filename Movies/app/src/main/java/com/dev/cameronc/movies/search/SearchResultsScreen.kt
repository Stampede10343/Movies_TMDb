package com.dev.cameronc.movies.search

import android.content.Context
import android.os.Parcelable
import androidx.transition.TransitionManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.util.AttributeSet
import com.dev.cameronc.androidutilities.BaseKey
import com.dev.cameronc.androidutilities.KeyboardHelper
import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.androidutilities.view.fadeIn
import com.dev.cameronc.movies.AppScreen
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.tv.TvSeriesScreen
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.search_results_screen.view.*
import timber.log.Timber
import javax.inject.Inject

class SearchResultsScreen : AppScreen, SearchView.OnQueryTextListener {

    @Inject
    lateinit var viewModel: SearchResultsViewModel
    @Inject
    internal lateinit var adapter: SearchResultAdapter
    @Inject
    lateinit var keyboardHelper: KeyboardHelper

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun viewReady() {
        if (isInEditMode) return
        MoviesApp.activityComponent.inject(this)

        search_results_toolbar.setNavigationOnClickListener { Navigator.getBackstack(context).goBack() }
        search_results_searchview.setOnQueryTextListener(this)
        val key = Backstack.getKey<Key>(context)
        search_results_searchview.setQuery(key.query, true)

        search_results_list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 1, androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false)
        val dividerItemDecoration = androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(context.getDrawable(R.drawable.item_divider)!!)
        search_results_list.addItemDecoration(dividerItemDecoration)
        search_results_list.adapter = adapter
        search_results_list.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (dy > 20) keyboardHelper.dismissKeyboard()
            }
        })

        adapter.movieClickListener = { Navigator.getBackstack(context).goTo(MovieDetailScreen.MovieDetailKey(it)) }
        adapter.tvClickListener = { Navigator.getBackstack(context).goTo(TvSeriesScreen.Key(it)) }

        viewModel.results()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is ScreenState.Loading -> showLoadingIndicator(true)
                        is ScreenState.Ready -> {
                            TransitionManager.beginDelayedTransition(this)
                            search_results_list.fadeIn()
                            showLoadingIndicator(false)
                            adapter.setResults(it.data)
                            if (viewState != null) restoreHierarchyState(viewState)
                            viewState = null
                        }
                    }
                }, { error -> Timber.e(error) }).disposeBy(this)

    }

    override fun getScreenName(): String = "Search Results Screen"

    override fun onQueryTextChange(query: String?): Boolean {
        viewModel.updateQuery(query)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        keyboardHelper.dismissKeyboard()
        return true
    }

    @Parcelize
    data class Key(internal val query: String) : BaseKey(), Parcelable {
        override fun layout(): Int = R.layout.search_results_screen
    }
}