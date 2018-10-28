package com.dev.cameronc.movies.di

import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.start.StartScreen
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

    fun inject(startScreen: StartScreen)
    fun inject(movieDetailScreen: MovieDetailScreen)
    fun inject(actorScreen: ActorScreen)
}