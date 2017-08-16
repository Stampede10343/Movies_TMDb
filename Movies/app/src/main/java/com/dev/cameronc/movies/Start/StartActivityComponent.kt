package com.dev.cameronc.movies.Start

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(StartActivityModule::class)) interface StartActivityComponent
{
    fun inject(activity: StartActivity)

}