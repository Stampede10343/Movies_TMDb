package com.dev.cameronc.movies

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.dev.cameronc.movies.di.AppModule
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MovieImageDownloader @Inject constructor(@Named(AppModule.SCREEN_WIDTH) val screenWidth: Int) {
    private var baseUrl: String = "https://image.tmdb.org/t/p/"
    private var posterWidth: String = "w500"

    fun load(posterPath: String?, view: ImageView): RequestBuilder<Drawable> {
        return if (posterPath != null) {

            val url = "$baseUrl$posterWidth/$posterPath"
            Timber.v(url)
            Glide.with(view).load(url)
        } else {
            Glide.with(view).load("")
        }
    }
}