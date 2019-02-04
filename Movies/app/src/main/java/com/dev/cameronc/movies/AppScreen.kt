package com.dev.cameronc.movies

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.dev.cameronc.androidutilities.AppNavigator
import com.dev.cameronc.androidutilities.view.BaseScreen
import javax.inject.Inject

abstract class AppScreen : BaseScreen {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    @Inject
    lateinit var navigator: AppNavigator

    protected fun showLoadingIndicator(visible: Boolean) {
        activity.findViewById<View>(R.id.loading_indicator).visibility = if (visible) View.VISIBLE else View.GONE
    }
}