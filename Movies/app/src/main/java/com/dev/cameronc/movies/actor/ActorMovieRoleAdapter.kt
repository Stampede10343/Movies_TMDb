package com.dev.cameronc.movies.actor

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.moviedb.data.actor.Role
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import javax.inject.Inject

class ActorMovieRoleAdapter @Inject constructor(private val imageDownloader: MovieImageDownloader) : RecyclerView.Adapter<ActorMovieRoleViewHolder>() {

    lateinit var movieRoleClickListener: (movieId: Long) -> Unit
    lateinit var items: List<Role>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ActorMovieRoleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.actor_movie_credit_item, parent, false)
        return ActorMovieRoleViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ActorMovieRoleViewHolder, position: Int) {
        val item = items[position]
        viewHolder.itemView.setOnClickListener { movieRoleClickListener.invoke(item.id) }
        imageDownloader.load(item.posterPath, viewHolder.movieImage)
                .apply(RequestOptions.centerCropTransform().placeholder(R.color.dark_grey))
                .into(viewHolder.movieImage)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].id
}

class ActorMovieRoleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieImage: ImageView = itemView.findViewById(R.id.actor_credit_image)
}
