package com.dev.cameronc.androidutilities.view

import android.content.Context
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.FrameLayout
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.statebundle.StateBundle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseScreen : FrameLayout, Bundleable {
    private val subscriptions = CompositeDisposable()
    protected var viewState: SparseArray<Parcelable>? = null

    @Inject
    lateinit var activity: AppCompatActivity
    @Inject
    lateinit var analyticTracker: AnalyticTrackingHelper

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract fun viewReady()
    abstract fun getScreenName(): String

    open fun handleBackPressed(): Boolean = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (!isInEditMode) viewReady()
        if (!isInEditMode) analyticTracker.trackScreenHit(getScreenName())
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

    override fun toBundle(): StateBundle {
        val sparseArray = SparseArray<Parcelable>()
        saveHierarchyState(sparseArray)
        return StateBundle().apply { putSparseParcelableArray("viewState", sparseArray) }
    }

    override fun fromBundle(bundle: StateBundle?) {
        viewState = bundle?.getSparseParcelableArray<Parcelable>("viewState") ?: SparseArray()
    }
}