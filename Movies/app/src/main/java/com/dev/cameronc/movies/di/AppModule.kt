package com.dev.cameronc.movies.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dev.cameronc.androidutilities.AnalyticTracker
import com.dev.cameronc.movies.LoggingAnalyticsTracker
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.model.MovieRepo
import com.dev.cameronc.movies.model.MovieRepository
import com.dev.cameronc.movies.model.MyObjectBox
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule(
        private val application: MoviesApp) {

    @Provides
    @Singleton
    fun provideMovieRepository(repo: MovieRepo): MovieRepository = repo

    @Provides
    fun context(): Context = application

    @Provides
    fun preferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Named(SCREEN_WIDTH)
    fun screenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    @Provides
    @Singleton
    fun objectBox(context: Context): BoxStore =
            MyObjectBox.builder().androidContext(context).build()

    @Provides
    fun analyticsTracker(analyticsTracker: LoggingAnalyticsTracker): AnalyticTracker = analyticsTracker

    companion object {
        const val SCREEN_WIDTH = "screenWidth"
    }
}