package com.dev.cameronc.movies.Start

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dev.cameronc.movies.Model.Movie
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R

class MovieCardAdapter(private val imageDownloader: MovieImageDownloader, private val results: MutableList<Movie>, private val movieAdapterListener: MovieAdapterListener) : RecyclerView.Adapter<MovieCardAdapter.MovieVH>()
{

    override fun getItemCount(): Int
    {
        return results.size
    }

    override fun onBindViewHolder(holder: MovieVH, position: Int)
    {
        holder.bind(results[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_card_view, parent, false)
        return MovieVH(view, imageDownloader, movieAdapterListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?)
    {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int)
            {
                val gridLayoutManager = rv.layoutManager as LinearLayoutManager
                val lastVisible = gridLayoutManager.findLastVisibleItemPosition()
                if(results.size - 6 < lastVisible)
                {
                    movieAdapterListener.loadMore()
                }
            }
        })
    }

    public fun setMovies(movies: List<Movie>)
    {
        this.results.clear()
        this.results.addAll(movies)
    }

    public fun addMovies(movies: List<Movie>)
    {
        notifyItemRangeInserted(results.size, movies.size)
        results.addAll(movies)
    }

    class MovieVH(view: View, val imageDownloader: MovieImageDownloader, val listener: MovieAdapterListener) : RecyclerView.ViewHolder(view)
    {
        val moviePoster: ImageView = view.findViewById(R.id.movie_card_poster)
        val movieTitle: TextView = view.findViewById(R.id.movie_name)

        fun bind(movie: Movie)
        {
            imageDownloader.load(movie.posterPath, moviePoster)?.centerCrop()?.placeholder(android.R.drawable.progress_horizontal)?.into(moviePoster)
            movieTitle.text = movie.title
            itemView.setOnClickListener { listener.onItemClicked(movie) }
        }
    }

    interface MovieAdapterListener
    {
        fun loadMore()
        fun onItemClicked(movie: Movie)
    }
}
