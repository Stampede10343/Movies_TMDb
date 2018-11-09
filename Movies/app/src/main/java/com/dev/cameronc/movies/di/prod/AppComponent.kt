package com.dev.cameronc.movies.di.prod

import com.dev.cameronc.movies.MoviesApp
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class, NetworkModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(app: MoviesApp): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }

    fun plusActivity(): ActivityComponent.Builder
}