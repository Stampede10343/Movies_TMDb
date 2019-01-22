package com.dev.cameronc.movies

import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Qualifier

data class Schedulers @Inject constructor(@Background val backgroundScheduler: Scheduler, @MainThread val mainThreadScheduler: Scheduler)

@Qualifier
annotation class Background

@Qualifier
annotation class MainThread