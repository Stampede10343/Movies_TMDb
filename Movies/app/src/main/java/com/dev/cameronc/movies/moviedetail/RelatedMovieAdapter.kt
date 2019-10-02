package com.dev.cameronc.movies.moviedetail

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMovie
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import javax.inject.Inject

class RelatedMovieAdapter @Inject constructor(private val movieImageDownloader: MovieImageDownloader) : androidx.recyclerview.widget.RecyclerView.Adapter<RelatedMovieViewHolder>() {
    lateinit var relatedMovies: List<SimilarMovie>
    lateinit var relatedMovieClickListener: (movieId: Long) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedMovieViewHolder =
            RelatedMovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.related_movie_item, parent, false))

    override fun onBindViewHolder(holder: RelatedMovieViewHolder, position: Int) {
        val item = relatedMovies[position]
        holder.itemView.setOnClickListener { relatedMovieClickListener.invoke(item.id) }
        movieImageDownloader.load(item.posterPath, holder.image)
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions().placeholder(R.drawable.black_gradient))
                .into(holder.image)
    }

    override fun getItemCount(): Int = relatedMovies.size

}

class RelatedMovieViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    internal val image: ImageView = itemView.findViewById(R.id.related_movie_image)
}
