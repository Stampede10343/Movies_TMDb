package com.dev.cameronc.movies.di

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass


@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)