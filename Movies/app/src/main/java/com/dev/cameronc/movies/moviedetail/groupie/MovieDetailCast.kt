package com.dev.cameronc.movies.moviedetail.groupie

import android.graphics.Rect
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.moviedb.data.movie.detail.Cast
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.moviedetail.MovieActorAdapter
import com.dev.cameronc.movies.toDp
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.movie_detail_cast.view.*

class MovieDetailCast(private val cast: List<Cast>, private val imageDownloader: MovieImageDownloader) : Item<ViewHolder>() {
    override fun getLayout(): Int = R.layout.movie_detail_cast

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.root.movie_detail_cast.adapter = MovieActorAdapter(imageDownloader).apply {
            setActors(cast)
            onActorClicked { Navigator.getBackstack(viewHolder.root.context).goTo(ActorScreen.ActorScreenKey(it)) }
        }
        val margin = 8.toDp()
        viewHolder.root.movie_detail_cast.addItemDecoration(MarginItemDecoration(Rect(margin, 0, margin, 0)))
    }
}