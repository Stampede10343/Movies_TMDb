package com.dev.cameronc.androidutilities

import javax.inject.Inject

class AnalyticTrackingHelper @Inject constructor(private val analyticTrackers: Set<@JvmSuppressWildcards AnalyticTracker>) : AnalyticTracker {

    override fun trackScreenHit(screenName: String) {
        analyticTrackers.forEach { it.trackScreenHit(screenName) }
    }

    override fun trackEvent(event: String) {
        analyticTrackers.forEach { it.trackEvent(event) }
    }
}