package com.dev.cameronc.movies.tv

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.tv.TvSeriesSeason

class SeasonTvPosterAdapter(private val seasons: List<TvSeriesSeason>, private val imageDownloader: MovieImageDownloader) : RecyclerView.Adapter<SeasonTvPosterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(inflater.inflate(R.layout.tv_series_season_item, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        seasons[position].apply {
            imageDownloader.load(posterPath, viewHolder.posterView).into(viewHolder.posterView)
            viewHolder.text.text = seasonName
        }
    }

    override fun getItemCount(): Int = seasons.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterView: ImageView = itemView.findViewById(R.id.series_item_poster)
        val text: TextView = itemView.findViewById(R.id.series_item_text)
    }
}
