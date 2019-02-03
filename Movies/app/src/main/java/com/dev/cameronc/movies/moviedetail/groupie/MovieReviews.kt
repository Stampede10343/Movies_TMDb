package com.dev.cameronc.movies.moviedetail.groupie

import android.view.View
import android.view.ViewGroup
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.moviedetail.MovieReviewAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.movie_detail_reviews.view.*

class MovieReviews(private val reviews: List<MovieReview>) : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.movie_detail_reviews

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (reviews.isEmpty()) {
            viewHolder.root.visibility = View.GONE
            viewHolder.root.layoutParams = ViewGroup.LayoutParams(0, 0)
        } else {
            viewHolder.root.movie_detail_reviews.adapter = MovieReviewAdapter().apply { setReviews(reviews) }
        }
    }
}