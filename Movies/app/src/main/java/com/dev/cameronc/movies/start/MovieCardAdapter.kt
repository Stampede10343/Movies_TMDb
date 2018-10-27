package com.dev.cameronc.movies.start

import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import com.dev.cameronc.moviedb.data.MovieResponseItem
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R

class MovieCardAdapter(private val imageDownloader: MovieImageDownloader, private val results: MutableList<MovieResponseItem>,
                       private val movieAdapterListener: MovieAdapterListener) : RecyclerView.Adapter<MovieCardAdapter.MovieVH>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        holder.bind(results[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_card_view, parent, false)
        return MovieVH(view, imageDownloader, movieAdapterListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val gridLayoutManager = rv.layoutManager as LinearLayoutManager
                val lastVisible = gridLayoutManager.findLastVisibleItemPosition()
                if (results.size - 6 < lastVisible) {
                    movieAdapterListener.loadMore()
                }
            }
        })
    }

    override fun getItemId(position: Int): Long = results[position].id

    fun setMovies(movies: List<MovieResponseItem>) {
        this.results.clear()
        this.results.addAll(movies)
        notifyDataSetChanged()
    }

    fun addMovies(movies: List<MovieResponseItem>) {
        val update = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = results[oldItemPosition].uniqueId == movies[newItemPosition].uniqueId
            override fun getOldListSize(): Int = results.size
            override fun getNewListSize(): Int = movies.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = results[oldItemPosition] == movies[newItemPosition]
        })
        results.addAll(movies)
        update.dispatchUpdatesTo(this)
    }

    class MovieVH(view: View, private val imageDownloader: MovieImageDownloader, private val listener: MovieAdapterListener) : RecyclerView.ViewHolder(view) {
        private val moviePoster: ImageView = view.findViewById(R.id.movie_card_poster)

        fun bind(movie: MovieResponseItem) {
            imageDownloader.load(movie.posterPath, moviePoster)
                    .apply(centerCropTransform().placeholder(android.R.drawable.progress_horizontal))
                    .into(moviePoster)
            itemView.setOnClickListener { listener.onItemClicked(movie) }
        }
    }

    interface MovieAdapterListener {
        fun loadMore()
        fun onItemClicked(movie: MovieResponseItem)
    }
}
