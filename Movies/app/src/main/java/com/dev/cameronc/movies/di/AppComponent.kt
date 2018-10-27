package com.dev.cameronc.movies.di

import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.moviedetail.MovieDetailActivity
import com.dev.cameronc.movies.start.StartActivityComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(app: MoviesApp): Builder

        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }

    fun startActivityComponent(): StartActivityComponent
    fun inject(movieDetailActivity: MovieDetailActivity)
}