package com.dev.cameronc.movies

import android.app.Application
import com.dev.cameronc.movies.di.ActivityComponent
import com.dev.cameronc.movies.di.AppComponent
import com.dev.cameronc.movies.di.AppModule
import com.dev.cameronc.movies.di.DaggerAppComponent
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber


class MoviesApp : Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this)
        app = this
        appComponent = DaggerAppComponent.builder().app(this).appModule(AppModule(this)).build()
    }

    fun getAppComponent(): AppComponent = appComponent

    companion object {
        lateinit var app: MoviesApp
        lateinit var activityComponent: ActivityComponent
    }

}
