package com.dev.cameronc.movies.moviedetail

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.R
import com.github.chrisbanes.photoview.PhotoView
import javax.inject.Inject

class GalleryAdapter @Inject constructor(private val imageDownloader: MovieImageDownloader) : androidx.viewpager.widget.PagerAdapter() {
    val imageUrls: MutableList<String> = emptyList<String>().toMutableList()
    var imageClickListener: (() -> Unit)? = null

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val imageView = PhotoView(container.context)
        imageView.setOnClickListener { imageClickListener?.invoke() }
        imageDownloader.loadOriginalImage(imageUrls[position], imageView)
                .apply(RequestOptions().placeholder(R.color.black))
                .into(imageView)
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    override fun getCount(): Int = imageUrls.size
}