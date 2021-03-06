package com.dev.cameronc.movies.di.test

import androidx.appcompat.app.AppCompatActivity
import com.dev.cameronc.movies.di.ActivityScope
import com.dev.cameronc.movies.di.prod.ActivityComponent
import com.dev.cameronc.movies.di.prod.ActivityModule
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface TestActivityComponent : ActivityComponent {

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: AppCompatActivity): TestActivityComponent.Builder

        fun build(): TestActivityComponent
    }
}