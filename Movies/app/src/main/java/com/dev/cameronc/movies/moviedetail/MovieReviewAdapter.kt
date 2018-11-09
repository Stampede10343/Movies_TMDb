package com.dev.cameronc.movies.moviedetail

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.movie.MovieReview
import javax.inject.Inject

class MovieReviewAdapter @Inject constructor() : RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder>() {
    private val reviews: MutableList<MovieReview> = emptyList<MovieReview>().toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReviewViewHolder(layoutInflater.inflate(R.layout.movie_review, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        viewHolder.author.text = review.author
        viewHolder.review.text = review.content
    }

    override fun getItemCount(): Int = reviews.size

    fun setReviews(newReviews: List<MovieReview>) {
        val callback = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean = reviews[oldPosition].id == newReviews[newPosition].id
            override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean = reviews[oldPosition] == newReviews[newPosition]
            override fun getOldListSize(): Int = reviews.size
            override fun getNewListSize(): Int = newReviews.size
        })

        reviews.clear()
        reviews.addAll(newReviews)
        callback.dispatchUpdatesTo(this)
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val author: TextView = itemView.findViewById(R.id.review_author)
        internal val review: TextView = itemView.findViewById(R.id.review_content)
    }
}