package com.dev.cameronc.movies.di.test

import android.support.v7.app.AppCompatActivity
import com.dev.cameronc.movies.di.ActivityScope
import com.dev.cameronc.movies.di.prod.ActivityComponent
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface TestActivityComponent : ActivityComponent {

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: AppCompatActivity): TestActivityComponent.Builder

        fun build(): TestActivityComponent
    }
}