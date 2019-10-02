package com.dev.cameronc.movies.moviedetail

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.cameronc.movies.R

class MovieGenreAdapter(private val genres: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<MovieGenreAdapter.GenreViewHolder>() {

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.title.text = genres[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder = GenreViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.genre_tag_layout, parent, false))

    override fun getItemCount(): Int = genres.size

    class GenreViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.genre_tag)
    }
}