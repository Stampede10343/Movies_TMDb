package com.dev.cameronc.movies

import com.dev.cameronc.androidutilities.AnalyticTracker
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingAnalyticsTracker @Inject constructor() : AnalyticTracker {

    override fun trackScreenHit(screenName: String) {
        Timber.v("Page hit: $screenName")
    }

    override fun trackEvent(event: String) {
        Timber.v("Event tracked: $event")
    }
}