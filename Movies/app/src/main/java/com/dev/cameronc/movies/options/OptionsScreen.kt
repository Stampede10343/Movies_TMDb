package com.dev.cameronc.movies.options

import android.content.Context
import android.util.AttributeSet
import com.dev.cameronc.androidutilities.BaseKey
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.movies.R

class OptionsScreen : BaseScreen {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun viewReady() {
    }

    class Key : BaseKey() {
        override fun layout(): Int = R.layout.options_screen
    }
}