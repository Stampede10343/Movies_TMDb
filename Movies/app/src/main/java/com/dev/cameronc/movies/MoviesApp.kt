package com.dev.cameronc.movies

import android.app.Application
import com.dev.cameronc.movies.Di.AppComponent
import com.dev.cameronc.movies.Di.AppModule
import com.dev.cameronc.movies.Di.DaggerAppComponent


class MoviesApp : Application()
{
    private lateinit var appComponent: AppComponent

    override fun onCreate()
    {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().app(this).appModule(AppModule(this)).build()
    }

    fun getAppComponent(): AppComponent
    {
        return appComponent
    }

}
