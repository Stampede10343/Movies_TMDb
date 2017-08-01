package com.dev.cameronc.movies.Di

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass


@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)