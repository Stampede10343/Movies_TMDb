package com.dev.cameronc.androidutilities.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseScreen : FrameLayout {
    private val subscriptions = CompositeDisposable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract fun viewReady()

    override fun onFinishInflate() {
        super.onFinishInflate()
        viewReady()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        subscriptions.dispose()
    }

    private fun autoUnsubscribe(subscription: Disposable) {
        subscriptions.add(subscription)
    }

    fun Disposable.disposeBy(screen: BaseScreen) {
        screen.autoUnsubscribe(this)
    }
}