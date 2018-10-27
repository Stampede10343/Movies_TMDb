package com.dev.cameronc.movies.moviedetail

import dagger.Module
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(MovieDetailComponent.MovieDetailModule::class)) interface MovieDetailComponent
{

    @Module abstract class MovieDetailModule
    {

    }
}
