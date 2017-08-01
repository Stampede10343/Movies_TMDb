package com.dev.cameronc.movies.Di

import com.dev.cameronc.movies.MoviesApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, AppModule::class, ActivityBuildersModule::class))
public interface AppComponent
{
    @Component.Builder
    public interface Builder
    {
        @BindsInstance fun app(app: MoviesApp): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
    fun inject(app: MoviesApp)
}