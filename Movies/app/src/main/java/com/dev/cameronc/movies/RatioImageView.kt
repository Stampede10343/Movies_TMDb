package com.dev.cameronc.movies

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet


class RatioImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(
        context, attrs, defStyle)
{

    private var ratio = 1.5f

    fun setRatio(ratio: Float)
    {
        this.ratio = ratio
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        setMeasuredDimension(measuredWidth, Math.round(measuredWidth * ratio))
    }
}