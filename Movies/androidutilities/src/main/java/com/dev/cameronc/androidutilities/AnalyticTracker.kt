package com.dev.cameronc.androidutilities

interface AnalyticTracker {
    fun trackScreenHit(screenName: String)
    fun trackEvent(event: String)
}