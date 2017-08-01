package com.dev.cameronc.movies.Di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dev.cameronc.movies.MoviesViewModelFactory
import com.dev.cameronc.movies.Start.StartViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule
{
    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    abstract fun startActivityViewModel(vm: StartViewModel): ViewModel

    @Binds
    abstract fun viewModelFactory(factory: MoviesViewModelFactory): ViewModelProvider.Factory
}

