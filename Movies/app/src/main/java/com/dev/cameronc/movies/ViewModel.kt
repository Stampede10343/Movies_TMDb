package com.dev.cameronc.movies

import timber.log.Timber

open class ViewModel {

    open fun onDestroy() {
        Timber.v("${this.javaClass.simpleName} destroyed")
    }
}