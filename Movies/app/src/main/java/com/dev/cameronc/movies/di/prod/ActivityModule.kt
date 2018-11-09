package com.dev.cameronc.movies.di.prod

import android.support.v7.app.AppCompatActivity
import com.dev.cameronc.androidutilities.KeyboardHelper
import com.dev.cameronc.movies.di.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    @ActivityScope
    @Provides
    fun keyboardHelper(activity: AppCompatActivity): KeyboardHelper = KeyboardHelper(activity)
}