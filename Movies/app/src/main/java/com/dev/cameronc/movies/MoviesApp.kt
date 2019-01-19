package com.dev.cameronc.movies

import android.annotation.SuppressLint
import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.dev.cameronc.androidutilities.network.ObservableConnectivityManager
import com.dev.cameronc.movies.di.prod.ActivityComponent
import com.dev.cameronc.movies.di.prod.AppComponent
import com.dev.cameronc.movies.di.prod.AppModule
import com.dev.cameronc.movies.di.prod.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber


class MoviesApp : Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        JodaTimeAndroid.init(this)

        LeakCanary.install(this)

        app = this

        val intentFilter = IntentFilter().apply { addAction(ConnectivityManager.CONNECTIVITY_ACTION) }
        registerReceiver(observableConnectivityManager, intentFilter)
    }

    fun getAppComponent(): AppComponent = appComponent

    companion object {
        lateinit var app: MoviesApp
        lateinit var activityComponent: ActivityComponent
    }

}
