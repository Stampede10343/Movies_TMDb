package com.dev.cameronc.movies.Di

import com.dev.cameronc.movies.MovieDetail.MovieDetailActivity
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.Start.StartActivityComponent
import com.dev.cameronc.movies.Start.StartActivityModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetworkModule::class))
public interface AppComponent
{
    @Component.Builder
    public interface Builder
    {
        @BindsInstance fun app(app: MoviesApp): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
    fun plus(activityModule: StartActivityModule): StartActivityComponent
    fun inject(movieDetailActivity: MovieDetailActivity)
}