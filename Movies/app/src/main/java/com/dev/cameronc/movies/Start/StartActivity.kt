package com.dev.cameronc.movies.Start

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.dev.cameronc.movies.Model.Movie
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import dagger.android.AndroidInjection
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class StartActivity : AppCompatActivity(), MovieCardAdapter.MovieAdapterListener
{
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var imageDownloader: MovieImageDownloader
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var viewModel: StartViewModel
    private lateinit var subscription: Subscription
    private var currentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?)
    {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        currentPage = savedInstanceState?.getInt("currentPage", 1) ?: 1
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StartViewModel::class.java)
        moviesRecyclerView = findViewById(R.id.start_movies)
        moviesRecyclerView.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.grid_columns))

        subscription = viewModel.getUpcomingMovies().observeOn(AndroidSchedulers.mainThread()).subscribe(
                { next -> moviesRecyclerView.adapter = MovieCardAdapter(imageDownloader, next.results, this) },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() })
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPage", currentPage)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        subscription.unsubscribe()
    }

    override fun loadMore()
    {
        viewModel.getUpcomingMovies(++currentPage).observeOn(AndroidSchedulers.mainThread()).subscribe({ next ->
            val adapter: MovieCardAdapter = moviesRecyclerView.adapter as MovieCardAdapter
            adapter.addMovies(next.results)
        },
        {
            error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
        })
    }

    override fun onItemClicked(movie: Movie)
    {
    }
}
