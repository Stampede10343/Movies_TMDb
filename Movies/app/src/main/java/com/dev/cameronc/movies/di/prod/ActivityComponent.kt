package com.dev.cameronc.movies.di.prod

import android.support.v7.app.AppCompatActivity
import com.dev.cameronc.movies.MainActivity
import com.dev.cameronc.movies.actor.ActorScreen
import com.dev.cameronc.movies.di.ActivityScope
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.options.OptionsScreen
import com.dev.cameronc.movies.options.ThemePickerFragment
import com.dev.cameronc.movies.search.SearchResultsScreen
import com.dev.cameronc.movies.start.StartScreen
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(startScreen: StartScreen)
    fun inject(movieDetailScreen: MovieDetailScreen)
    fun inject(actorScreen: ActorScreen)
    fun inject(searchResultsScreen: SearchResultsScreen)
    fun inject(optionsScreen: OptionsScreen)
    fun inject(themePickerFragment: ThemePickerFragment)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: AppCompatActivity): Builder

        fun build(): ActivityComponent
    }
}