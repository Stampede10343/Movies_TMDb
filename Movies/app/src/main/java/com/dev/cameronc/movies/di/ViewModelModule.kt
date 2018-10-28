package com.dev.cameronc.movies.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dev.cameronc.movies.MoviesViewModelFactory
import com.dev.cameronc.movies.actor.ActorViewModel
import com.dev.cameronc.movies.moviedetail.MovieDetailViewModel
import com.dev.cameronc.movies.start.StartViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    abstract fun startActivityViewModel(vm: StartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun movieDetailViewModel(vm: MovieDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ActorViewModel::class)
    abstract fun actorViewModel(vm: ActorViewModel): ViewModel

    @Binds
    abstract fun viewModelFactory(factory: MoviesViewModelFactory): ViewModelProvider.Factory
}

