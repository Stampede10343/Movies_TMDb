package com.dev.cameronc.movies.moviedetail.groupie

import android.graphics.Rect
import android.view.View
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMovie
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.moviedetail.RelatedMovieAdapter
import com.dev.cameronc.movies.toDp
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.movie_detail_related.view.*

class RelatedMoviesItem(private val similarMovies: List<SimilarMovie>, private val movieImageDownloader: MovieImageDownloader) : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.movie_detail_related

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (similarMovies.isNotEmpty()) {
            viewHolder.root.movie_details_related.adapter = RelatedMovieAdapter(movieImageDownloader).apply {
                relatedMovies = similarMovies
                relatedMovieClickListener = { Navigator.getBackstack(viewHolder.root.context).goTo(MovieDetailScreen.MovieDetailKey(it)) }
            }
            val margin = 8.toDp()
            viewHolder.root.movie_details_related.addItemDecoration(MarginItemDecoration(Rect(margin, 0, margin, 0)))

        } else viewHolder.root.visibility = View.GONE
    }
}