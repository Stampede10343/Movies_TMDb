package com.dev.cameronc.androidutilities.view

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class MarginItemDecoration(private val margin: Rect) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.layoutManager as LinearLayoutManager
        val position = adapter.getPosition(view)
        outRect.bottom = margin.bottom
        outRect.top = margin.top
        if (position != 0) outRect.left = (margin.left / 2f).toInt()
        if (position != adapter.itemCount - 1) outRect.right = (margin.right / 2f).toInt()
    }
}