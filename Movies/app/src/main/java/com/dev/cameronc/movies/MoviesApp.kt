package com.dev.cameronc.movies

import android.app.Application
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
        appComponent = DaggerAppComponent
                .builder()
                .app(this)
                .appModule(AppModule(this))
                .build()
    }

    fun getAppComponent(): AppComponent = appComponent

    companion object {
        lateinit var app: MoviesApp
        lateinit var activityComponent: ActivityComponent
    }

}
