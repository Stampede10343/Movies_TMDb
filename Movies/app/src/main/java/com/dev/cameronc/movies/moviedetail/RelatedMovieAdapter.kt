package com.dev.cameronc.movies.moviedetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.moviedb.data.Result
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import javax.inject.Inject

class RelatedMovieAdapter @Inject constructor(private val movieImageDownloader: MovieImageDownloader) : RecyclerView.Adapter<RelatedMovieViewHolder>() {
    lateinit var relatedMovies: List<Result>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedMovieViewHolder =
            RelatedMovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.related_movie_item, parent, false))

    override fun onBindViewHolder(holder: RelatedMovieViewHolder, position: Int) {
        val item = relatedMovies[position]
        movieImageDownloader.load(item.posterPath, holder.image)
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions().placeholder(R.drawable.black_gradient))
                .into(holder.image)
    }

    override fun getItemCount(): Int = relatedMovies.size

    override fun getItemId(position: Int): Long = relatedMovies[position].id.toLong()
}

class RelatedMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal val image: ImageView = itemView.findViewById(R.id.related_movie_image)
}