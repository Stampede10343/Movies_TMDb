package com.dev.cameronc.movies.start

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions.centerCropTransform
import com.dev.cameronc.androidutilities.RecyclerViewDiffCalculator
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.movie.UpcomingMovie

class MovieCardAdapter(private val imageDownloader: MovieImageDownloader, private val results: MutableList<UpcomingMovie>,
                       private val movieAdapterListener: MovieAdapterListener) : RecyclerView.Adapter<MovieCardAdapter.MovieVH>() {

    private var isLoadingMore = false

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_card_view, parent, false)
        return MovieVH(view, imageDownloader, movieAdapterListener)
    }

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int = results.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val gridLayoutManager = rv.layoutManager as LinearLayoutManager
                val lastVisible = gridLayoutManager.findLastVisibleItemPosition()
                if (results.size - 6 < lastVisible && !isLoadingMore) {
                    isLoadingMore = true
                    movieAdapterListener.loadMore()
                }
            }
        })
    }

    override fun getItemId(position: Int): Long = results[position].id

    fun addMovies(movies: List<UpcomingMovie>) {
        val update = RecyclerViewDiffCalculator<UpcomingMovie>().calculateDiff(results, movies)
        results.clear()
        results.addAll(movies)
        update.dispatchUpdatesTo(this)

        isLoadingMore = false
    }

    class MovieVH(view: View, private val imageDownloader: MovieImageDownloader, private val listener: MovieAdapterListener) : RecyclerView.ViewHolder(view) {
        private val moviePoster: ImageView = view.findViewById(R.id.movie_card_poster)

        fun bind(movie: UpcomingMovie) {
            imageDownloader.load(movie.posterPath, moviePoster)
                    .apply(centerCropTransform().placeholder(android.R.drawable.progress_horizontal))
                    .into(moviePoster)
            itemView.setOnClickListener { listener.onItemClicked(movie.id) }
        }

    }

    interface MovieAdapterListener {
        fun loadMore()
        fun onItemClicked(tmdbId: Long)
    }
}
