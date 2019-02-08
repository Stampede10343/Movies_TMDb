package com.dev.cameronc.movies.di.prod

import android.content.Context
import com.dev.cameronc.movies.di.Disk
import com.dev.cameronc.movies.di.Network
import com.dev.cameronc.movies.model.*
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
    fun tvRepository(tvRepository: AppTvRepository): TvRepository = tvRepository

    @Provides
    @Singleton
    fun tvDiskDataStore(tvDataStore: ObjectBoxTvDataStore): TvDataStore = tvDataStore

    @Provides
    @Singleton
    fun objectBox(context: Context): BoxStore = MyObjectBox.builder().androidContext(context).build()

    @Provides
    @Network
    @Singleton
    fun networkMovieDataSource(networkMovieDataSource: NetworkMovieDataSource): MovieDataSource = networkMovieDataSource

    @Provides
    @Disk
    @Singleton
    fun diskMovieDataSource(boxDataStore: MovieBoxDataStore): MovieDataSource = boxDataStore
}