package com.dev.cameronc.movies.di.prod

import android.content.Context
import com.dev.cameronc.movies.model.MovieRepo
import com.dev.cameronc.movies.model.MovieRepository
import com.dev.cameronc.movies.model.MyObjectBox
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideMovieRepository(repo: MovieRepo): MovieRepository = repo

    @Provides
    @Singleton
    fun objectBox(context: Context): BoxStore = MyObjectBox.builder().androidContext(context).build()
}