package com.dev.cameronc.movies

import android.os.Handler
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

open class ViewModel {

    open fun onDestroy() {
        Timber.v("${this.javaClass.simpleName} destroyed")
        if (BuildConfig.DEBUG) {
            Handler().postDelayed({ LeakCanary.installedRefWatcher().watch(this, javaClass.simpleName) }, 1000)
        }
    }
}