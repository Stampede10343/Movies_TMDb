package com.dev.cameronc.movies.moviedetail.groupie

import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.moviedetail.MovieGenreAdapter
import com.dev.cameronc.movies.toDp
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.movie_detail_info.view.*

class MovieDetailInfo(val movieTitle: String, val rating: String, val runtime: String, val voteAverage: String,
                      val genres: List<String>, val description: String) : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.movie_detail_info

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.root.movie_detail_title.text = movieTitle
        viewHolder.root.movie_detail_rating.visibility = View.VISIBLE
        viewHolder.root.movie_detail_rating.text = rating
        viewHolder.root.movie_detail_runtime.visibility = View.VISIBLE
        viewHolder.root.movie_detail_runtime.text = runtime
        viewHolder.root.movie_detail_rating_average.text = voteAverage
        viewHolder.root.movie_detail_genre_list.visibility = View.VISIBLE
        viewHolder.root.movie_detail_genre_list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(viewHolder.root.context, 1, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        val margin = 8.toDp()
        viewHolder.root.movie_detail_genre_list.addItemDecoration(MarginItemDecoration(Rect(margin, 0, margin, 0)))
        viewHolder.root.movie_detail_genre_list.adapter = MovieGenreAdapter(genres)
        viewHolder.root.movie_detail_description.text = description
    }
}