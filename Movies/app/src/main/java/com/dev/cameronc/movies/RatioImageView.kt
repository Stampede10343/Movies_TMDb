package com.dev.cameronc.movies

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet


class RatioImageView : AppCompatImageView
{
    private var ratio = 1.5f

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs)
    {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.RatioImageView, 0, 0)
        ratio = array.getFloat(R.styleable.RatioImageView_ratio, 1.5f)

        array.recycle()
    }

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