package com.dev.cameronc.androidutilities.view

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import com.dev.cameronc.androidutilities.R


class RatioImageView : AppCompatImageView {
    private var ratio = 1.5f

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs) {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.RatioImageView, style, 0)
        ratio = array.getFloat(R.styleable.RatioImageView_ratio, 1.5f)

        array.recycle()
    }

    fun setRatio(ratio: Float) {
        this.ratio = ratio
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        setMeasuredDimension(measuredWidth, Math.round(measuredWidth * ratio))
    }
}