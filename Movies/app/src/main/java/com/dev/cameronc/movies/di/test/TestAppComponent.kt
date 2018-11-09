package com.dev.cameronc.movies.di.test

import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.di.prod.AppComponent
import com.dev.cameronc.movies.di.prod.AppModule
import com.dev.cameronc.movies.di.prod.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, TestDataModule::class, NetworkModule::class])
interface TestAppComponent : AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(app: MoviesApp): TestAppComponent.Builder

        fun appModule(appModule: AppModule): TestAppComponent.Builder
        fun build(): TestAppComponent
    }

    fun plusTestActivity(): TestActivityComponent.Builder
}