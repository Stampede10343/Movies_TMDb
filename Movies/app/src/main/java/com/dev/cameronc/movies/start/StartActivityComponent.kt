package com.dev.cameronc.movies.start

import dagger.Subcomponent

@Subcomponent
interface StartActivityComponent {
    fun inject(activity: StartActivity)

}