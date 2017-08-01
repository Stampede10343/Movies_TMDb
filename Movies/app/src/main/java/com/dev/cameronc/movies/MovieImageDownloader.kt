package com.dev.cameronc.movies

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.DrawableTypeRequest
import com.bumptech.glide.Glide
import com.dev.cameronc.movies.Model.ConfigurationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class MovieImageDownloader @Inject constructor(val screenWidth: Int)
{
    var baseUrl: String? = null
    var posterWidth: String? = null

    fun init(response: ConfigurationResponse)
    {
        baseUrl = response.images.baseUrl
        posterWidth = response.images.posterSizes.find { it.removePrefix("w").toInt() >= screenWidth / 3 }
    }

    fun load(posterPath: String?, view: ImageView): DrawableTypeRequest<String>?
    {
        if (baseUrl != null && posterPath != null)
        {
            return Glide.with(view.context).load(baseUrl + posterWidth + '/' + posterPath)
        }
        else
        {
            Log.i(javaClass.name, "Image Loader not initialized")
            return null
        }

        return null
    }
}