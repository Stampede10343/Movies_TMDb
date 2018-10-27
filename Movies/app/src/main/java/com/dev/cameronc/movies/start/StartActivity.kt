package com.dev.cameronc.movies.start

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.widget.Toast
import com.dev.cameronc.moviedb.data.MovieResponseItem
import com.dev.cameronc.movies.BaseActivity
import com.dev.cameronc.movies.moviedetail.MovieDetailActivity
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_start.*
import timber.log.Timber
import javax.inject.Inject


class StartActivity : BaseActivity(), MovieCardAdapter.MovieAdapterListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    private lateinit var viewModel: StartViewModel
    private val subscriptions: CompositeDisposable = CompositeDisposable()
    private var currentPage: Int = 1
    private lateinit var moviesAdapter: MovieCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        getApp().getAppComponent()
                .startActivityComponent()
                .inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        movie_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.onSearchEntered(query)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            Timber.i(response.toString())
                        }, { err ->
                            Timber.e(err.message)
                        })
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        currentPage = savedInstanceState?.getInt("currentPage", 1) ?: 1
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(StartViewModel::class.java)

        moviesAdapter = MovieCardAdapter(imageDownloader, emptyList<MovieResponseItem>().toMutableList(), this)
        start_movies.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.grid_columns))
        start_movies.adapter = moviesAdapter

        subscriptions.add(viewModel.getUpcomingMovies().observeOn(AndroidSchedulers.mainThread()).subscribe(
                { movies -> moviesAdapter.addMovies(movies) },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPage", currentPage)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

    override fun loadMore() {
        viewModel.getUpcomingMovies(++currentPage)
    }

    override fun onItemClicked(movie: MovieResponseItem) {
        val movieDetailIntent = MovieDetailActivity.newIntent(this, movie)
        startActivity(movieDetailIntent)
    }
}
