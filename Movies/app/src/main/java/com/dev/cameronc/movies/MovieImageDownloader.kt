package com.dev.cameronc.movies

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dev.cameronc.movies.di.prod.AppModule
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MovieImageDownloader @Inject constructor(@Named(AppModule.SCREEN_WIDTH) val screenWidth: Int) {
    private var baseUrl: String = "https://image.tmdb.org/t/p/"

    fun load(posterPath: String?, view: ImageView): RequestBuilder<Drawable> {
        return if (posterPath != null) {

            val url = "$baseUrl$posterWidth/$posterPath"
            Timber.v(url)
            GlideApp.with(view).load(url)
        } else {
            GlideApp.with(view).load("")
        }
    }

    fun loadBackdrop(backdropPath: String?, view: ImageView): RequestBuilder<Drawable> {
        return if (backdropPath != null) {

            val url = "$baseUrl$backdropWidth/$backdropPath"
            Timber.v(url)
            GlideApp.with(view).load(url)
        } else {
            GlideApp.with(view).load("")
        }
    }

    fun loadOriginalImage(posterPath: String?, view: ImageView): RequestBuilder<Drawable> {
        return if (posterPath != null) {

            val url = "$baseUrl$originalWidth/$posterPath"
            Timber.v(url)
            GlideApp.with(view)
                    .load(url)
                    .transition(DrawableTransitionOptions().crossFade())
                    .format(DecodeFormat.PREFER_ARGB_8888)
        } else {
            GlideApp.with(view).load("")
        }
    }

    companion object {
        private const val posterWidth: String = "w500"
        private const val backdropWidth: String = "w780"
        private const val originalWidth: String = "original"
    }
}