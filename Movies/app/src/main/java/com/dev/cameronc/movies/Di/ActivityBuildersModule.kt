package com.dev.cameronc.movies.Di

import android.app.Activity
import com.dev.cameronc.movies.Start.StartActivity
import com.dev.cameronc.movies.Start.StartActivityComponent
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class ActivityBuildersModule
{
    @Binds
    @IntoMap
    @ActivityKey(StartActivity::class)
    abstract fun bindStartActivity(builder: StartActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}