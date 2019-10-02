package com.dev.cameronc.movies.search

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.MultiSearchResult
import javax.inject.Inject

internal class SearchResultAdapter @Inject constructor(private val imageDownloader: MovieImageDownloader) : androidx.recyclerview.widget.RecyclerView.Adapter<SearchResultAdapter.MultiSearchViewHolder>() {
    private val searchResults: MutableList<MultiSearchResult> = emptyList<MultiSearchResult>().toMutableList()
    lateinit var movieClickListener: (id: Long) -> Unit
    lateinit var tvClickListener: (id: Long) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.multi_search_item, parent, false)
        return when (viewType) {
            1 -> MultiSearchViewHolder.MovieSearchViewHolder(imageDownloader, view)
            2 -> MultiSearchViewHolder.TelevisionSearchViewHolder(imageDownloader, view)
            else -> MultiSearchViewHolder.MovieSearchViewHolder(imageDownloader, view)
        }
    }

    override fun onBindViewHolder(holder: MultiSearchViewHolder, position: Int) {
        val result = searchResults[position]
        when (result) {
            is MultiSearchResult.MovieSearchResult -> (holder as MultiSearchViewHolder.MovieSearchViewHolder).bind(result, movieClickListener)
            is MultiSearchResult.TelevisionSearchResult -> (holder as MultiSearchViewHolder.TelevisionSearchViewHolder).bind(result, tvClickListener)
        }
    }

    override fun getItemCount(): Int = searchResults.size

    override fun getItemViewType(position: Int): Int {
        val searchResult = searchResults[position]

        return when (searchResult) {
            is MultiSearchResult.MovieSearchResult -> 1
            is MultiSearchResult.TelevisionSearchResult -> 2
        }
    }

    fun setResults(results: List<MultiSearchResult>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean = searchResults[oldPosition].id == results[newPosition].id
            override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean = searchResults[oldPosition] == results[newPosition]
            override fun getOldListSize(): Int = searchResults.size
            override fun getNewListSize(): Int = results.size
        })

        searchResults.clear()
        searchResults.addAll(results)
        diff.dispatchUpdatesTo(this)
    }

    internal sealed class MultiSearchViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        internal val image: ImageView = itemView.findViewById(R.id.multi_result_item_image)
        internal val title: TextView = itemView.findViewById(R.id.multi_result_item_title)

        class MovieSearchViewHolder(private val imageDownloader: MovieImageDownloader, itemView: View) : MultiSearchViewHolder(itemView) {
            fun bind(movieSearchResult: MultiSearchResult.MovieSearchResult, listener: (id: Long) -> Unit) {
                title.text = movieSearchResult.title
                imageDownloader.load(movieSearchResult.moviePosterPath, image)
                        .apply(RequestOptions().centerCrop().placeholder(R.color.dark_grey))
                        .into(image)

                itemView.setOnClickListener { listener.invoke(movieSearchResult.movieId) }
            }
        }

        class TelevisionSearchViewHolder(private val imageDownloader: MovieImageDownloader, itemView: View) : MultiSearchViewHolder(itemView) {
            fun bind(televisionSearchResult: MultiSearchResult.TelevisionSearchResult, clickListener: (id: Long) -> Unit) {
                title.text = televisionSearchResult.title
                imageDownloader.load(televisionSearchResult.tvPosterPath, image)
                        .apply(RequestOptions().centerCrop().placeholder(R.color.dark_grey))
                        .into(image)

                itemView.setOnClickListener { clickListener.invoke(televisionSearchResult.tvId) }
            }
        }
    }
}