package com.dev.cameronc.movies

import android.app.Activity
import android.app.Application
import com.dev.cameronc.movies.Di.AppComponent
import com.dev.cameronc.movies.Di.AppModule
import com.dev.cameronc.movies.Di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


class MoviesApp : Application(), HasActivityInjector
{
    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    private lateinit var appComponent: AppComponent
    override fun onCreate()
    {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().app(this).appModule(AppModule(this)).build()
        appComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>
    {
        return activityInjector
    }
}
