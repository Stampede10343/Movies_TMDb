package com.dev.cameronc.movies.Start

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(StartActivityModule::class))
interface StartActivityComponent: AndroidInjector<StartActivity>
{
    @Subcomponent.Builder
     abstract class Builder: AndroidInjector.Builder<StartActivity>()
}