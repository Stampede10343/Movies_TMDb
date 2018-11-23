package com.dev.cameronc.movies.actor

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.moviedb.data.tv.TvCast
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import javax.inject.Inject

class ActorTvRoleAdapter @Inject constructor(private val imageDownloader: MovieImageDownloader) : RecyclerView.Adapter<ActorTvRoleViewHolder>() {

    lateinit var tvRoleClickListener: (tvId: Long) -> Unit
    lateinit var items: List<TvCast>

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ActorTvRoleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.actor_movie_credit_item, parent, false)
        return ActorTvRoleViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ActorTvRoleViewHolder, position: Int) {
        val item = items[position]
        viewHolder.itemView.setOnClickListener { tvRoleClickListener.invoke(item.id) }
        imageDownloader.load(item.posterPath, viewHolder.movieImage)
                .apply(RequestOptions.centerCropTransform().placeholder(R.color.dark_grey))
                .into(viewHolder.movieImage)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].id
}

class ActorTvRoleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val movieImage: ImageView = itemView.findViewById(R.id.actor_credit_image)
}