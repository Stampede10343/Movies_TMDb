package com.dev.cameronc.movies

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.dev.cameronc.androidutilities.network.ObservableConnectivityManager
import com.dev.cameronc.movies.di.prod.ActivityComponent
import com.dev.cameronc.movies.di.prod.AppComponent
import com.dev.cameronc.movies.di.prod.AppModule
import com.dev.cameronc.movies.di.prod.DaggerAppComponent
import com.dev.cameronc.movies.di.test.DaggerTestAppComponent
import com.squareup.leakcanary.LeakCanary
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import javax.inject.Inject


class MoviesApp : Application() {
    @Inject
    lateinit var observableConnectivityManager: ObservableConnectivityManager
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        JodaTimeAndroid.init(this)

        LeakCanary.install(this)

        app = this
        setupComponent()
        appComponent.inject(this)

        val intentFilter = IntentFilter().apply { addAction(ConnectivityManager.CONNECTIVITY_ACTION) }
        registerReceiver(observableConnectivityManager, intentFilter)
    }

    private fun setupComponent() {
        appComponent = if (isTestComponentEnabled()) {
            Timber.d("Test component installed")
            DaggerTestAppComponent.builder()
                    .app(this)
                    .appModule(AppModule(this))
                    .build()
        } else {
            Timber.d("Application component installed")
            DaggerAppComponent.builder()
                    .app(this)
                    .appModule(AppModule(this))
                    .build()
        }
    }

    private fun isTestComponentEnabled() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(COMPONENT, false)

    fun getAppComponent(): AppComponent = appComponent

    companion object {
        const val COMPONENT = "component"
        lateinit var app: MoviesApp
        lateinit var activityComponent: ActivityComponent
    }

}
