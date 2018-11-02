package com.dev.cameronc.movies.moviedetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.moviedb.data.Cast
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import javax.inject.Inject

class MovieActorAdapter @Inject constructor(private val imageDownloader: MovieImageDownloader) : RecyclerView.Adapter<MovieActorAdapter.ActorViewHolder>() {
    private lateinit var cast: List<Cast>
    private lateinit var clickListener: (actorId: Long) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder =
            ActorViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.actor_grid_item, parent, false))

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = cast[position]
        holder.actorName.text = actor.name

        imageDownloader.load(actor.profilePath, holder.imageView)
                .apply(RequestOptions().centerCrop())
                .apply(RequestOptions().placeholder(R.drawable.empty_profile))
                .into(holder.imageView)

        holder.imageView.setOnClickListener { clickListener.invoke(actor.id) }
    }

    override fun getItemCount(): Int = cast.size

    fun setActors(cast: List<Cast>) {
        this.cast = cast.asSequence().toHashSet().asSequence().sortedBy { it.order }.toList()
    }

    fun onActorClicked(clickListener: (actorId: Long) -> Unit) {
        this.clickListener = clickListener
    }

    class ActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val imageView: ImageView = itemView.findViewById(R.id.actor_item_image)
        internal val actorName: TextView = itemView.findViewById(R.id.actor_item_name)
    }
}