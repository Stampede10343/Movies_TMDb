package com.dev.cameronc.movies.Di

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dev.cameronc.movies.Model.MovieRepo
import com.dev.cameronc.movies.Model.MovieRepositoy
import com.dev.cameronc.movies.Model.Room.MovieDao
import com.dev.cameronc.movies.Model.Room.MovieDatabase
import com.dev.cameronc.movies.MoviesApp
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class)) class AppModule(
        val application: MoviesApp)
{

    @Provides @Singleton fun provideMovieRepository(repo: MovieRepo): MovieRepositoy = repo

    @Provides @Singleton fun provideMovieDao(context: Context): MovieDao
    {
        return Room.databaseBuilder(context, MovieDatabase::class.java, "movieDb").build().movieDao()
    }

    @Provides fun context(): Context
    {
        return application
    }

    @Provides fun preferences(context: Context): SharedPreferences
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides @Named(SCREEN_WIDTH) fun screenWidth(context: Context): Int
    {
        return context.resources.displayMetrics.widthPixels
    }

    companion object
    {
        const val SCREEN_WIDTH = "screenWidth"
    }
}