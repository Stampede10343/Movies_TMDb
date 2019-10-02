package com.dev.cameronc.movies.di.prod

import androidx.appcompat.app.AppCompatActivity
import com.dev.cameronc.androidutilities.AppNavigator
import com.dev.cameronc.androidutilities.KeyboardHelper
import com.dev.cameronc.androidutilities.SnackBarHelper
import com.dev.cameronc.androidutilities.StackNavigator
import com.dev.cameronc.movies.AndroidSnackBarHelper
import com.dev.cameronc.movies.di.ActivityScope
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    @ActivityScope
    @Provides
    fun keyboardHelper(activity: AppCompatActivity): KeyboardHelper = KeyboardHelper(activity)

    @ActivityScope
    @Provides
    fun snackbarHelper(snackBarHelper: AndroidSnackBarHelper): SnackBarHelper = snackBarHelper

    @ActivityScope
    @Provides
    fun backstack(activity: AppCompatActivity): Backstack = Navigator.getBackstack(activity)

    @ActivityScope
    @Provides
    fun appNavigator(stackNavigator: StackNavigator): AppNavigator = stackNavigator
}