package com.dev.cameronc.movies.di.prod

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.dev.cameronc.androidutilities.AnalyticTracker
import com.dev.cameronc.movies.Background
import com.dev.cameronc.movies.LoggingAnalyticsTracker
import com.dev.cameronc.movies.MainThread
import com.dev.cameronc.movies.MoviesApp
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
class AppModule(private val application: MoviesApp) {

    @Provides
    fun context(): Context = application

    @Provides
    fun preferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Named(SCREEN_WIDTH)
    fun screenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    @Provides
    @IntoSet
    fun analyticsTracker(analyticsTracker: LoggingAnalyticsTracker): AnalyticTracker = analyticsTracker

    @Provides
    fun connectivityManager(): ConnectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Background
    fun backgroundScheduler(): Scheduler = Schedulers.io()

    @Provides
    @MainThread
    fun mainThreadScheduler(): Scheduler = AndroidSchedulers.mainThread()

    companion object {
        const val SCREEN_WIDTH = "screenWidth"
    }
}