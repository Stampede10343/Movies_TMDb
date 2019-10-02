package com.dev.cameronc.androidutilities.view

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class MarginItemDecoration(private val margin: Rect) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val position = adapter.getPosition(view)
        outRect.bottom = margin.bottom
        outRect.top = margin.top
        if (position != 0) outRect.left = (margin.left / 2f).toInt()
        if (position != adapter.itemCount - 1) outRect.right = (margin.right / 2f).toInt()
    }
}