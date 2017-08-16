package com.dev.cameronc.movies.MovieDetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.cameronc.movies.R

class MovieGenreAdapter(val genres: MutableList<String>) : RecyclerView.Adapter<MovieGenreAdapter.GenreViewHolder>()
{

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int)
    {
        holder.title.text = genres[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder
    {
        return GenreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.genre_tag_layout, parent, false))
    }

    override fun getItemCount(): Int = genres.size

    class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val title: TextView = view.findViewById(R.id.genre_tag)
    }
}