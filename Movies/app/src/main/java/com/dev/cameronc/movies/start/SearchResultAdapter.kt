package com.dev.cameronc.movies.start

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import javax.inject.Inject

class SearchResultAdapter @Inject constructor(private val imageDownloader: MovieImageDownloader) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    lateinit var resultClickListener: (movieId: Long) -> Unit

    private val searchResults: MutableList<SearchResult> = emptyList<SearchResult>().toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SearchResultViewHolder(layoutInflater.inflate(R.layout.search_result_item, parent, false))
    }

    override fun onBindViewHolder(viewHolder: SearchResultViewHolder, position: Int) {
        val item = searchResults[position]
        imageDownloader.load(item.imagePath, viewHolder.thumbnail)
                .apply(RequestOptions().placeholder(R.color.dark_grey))
                .apply(RequestOptions().centerCrop())
                .into(viewHolder.thumbnail)
        viewHolder.text.text = item.title

        viewHolder.itemView.setOnClickListener { resultClickListener.invoke(item.id) }
    }

    override fun getItemCount(): Int = searchResults.size

    fun setSearchResults(results: List<SearchResult>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean = searchResults[oldPosition].id == results[newPosition].id
            override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean = searchResults[oldPosition] == results[newPosition]
            override fun getOldListSize(): Int = searchResults.size
            override fun getNewListSize(): Int = results.size
        }, true)

        searchResults.clear()
        searchResults.addAll(results)
        diff.dispatchUpdatesTo(this)
    }

    class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.search_result_thumbnail)
        val text: TextView = itemView.findViewById(R.id.search_result_item_text)
    }
}
