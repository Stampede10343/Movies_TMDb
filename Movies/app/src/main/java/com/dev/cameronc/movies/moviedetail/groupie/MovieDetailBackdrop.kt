package com.dev.cameronc.movies.moviedetail.groupie

import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.movie_detail_backdrop.view.*

class MovieDetailBackdrop(private val posterPath: String?, private val imageDownloader: MovieImageDownloader) : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.movie_detail_backdrop

    override fun bind(viewHolder: ViewHolder, position: Int) {
        imageDownloader.load(posterPath, viewHolder.root.movie_detail_backdrop).into(viewHolder.root.movie_detail_backdrop)
    }
}