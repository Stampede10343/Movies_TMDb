package com.dev.cameronc.movies.di

import android.app.Activity
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.search.SearchResultsScreen
import com.dev.cameronc.movies.start.StartScreen
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface ActivityComponent {
    fun inject(startScreen: StartScreen)
    fun inject(movieDetailScreen: MovieDetailScreen)
    fun inject(actorScreen: ActorScreen)
    fun inject(searchResultsScreen: SearchResultsScreen)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: Activity): Builder

        fun build(): ActivityComponent
    }
}