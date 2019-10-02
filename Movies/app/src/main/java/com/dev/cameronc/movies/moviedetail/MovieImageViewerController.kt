package com.dev.cameronc.movies.moviedetail

import android.os.Bundle
import android.os.Parcelable
import androidx.viewpager.widget.ViewPager
import com.dev.cameronc.androidutilities.view.fadeAndSetGone
import com.dev.cameronc.androidutilities.view.fadeIn

class MovieImageViewerController(private val viewPager: androidx.viewpager.widget.ViewPager) {
    var viewState: Parcelable? = null

    fun show(imageUrls: List<String>, galleryAdapter: GalleryAdapter) {
        galleryAdapter.imageUrls.addAll(imageUrls)
        galleryAdapter.imageClickListener = { hide() }
        viewPager.adapter = galleryAdapter
        viewPager.offscreenPageLimit = 2
        viewPager.setPageTransformer(false) { view, position ->
            view.alpha = Math.max(1 - Math.abs(position), 0.2f)
        }

        viewPager.onRestoreInstanceState(viewState)
        viewPager.fadeIn()
        viewPager.bringToFront()
    }

    fun hide() {
        viewState = viewPager.onSaveInstanceState()
        viewPager.fadeAndSetGone()
    }

    fun saveState(bundle: Bundle) {
        viewState = viewPager.onSaveInstanceState()
        bundle.putParcelable("viewPager", viewState)
    }

    fun restoreState(state: Bundle) {
        viewState = state.getParcelable("viewPager")
    }
}