package com.dev.cameronc.movies.Di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dev.cameronc.movies.Model.MovieDbApi
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.Start.StartActivityComponent
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class),
        subcomponents = arrayOf(StartActivityComponent::class)) class AppModule(val application: MoviesApp)
{
    @Provides @Singleton fun provideMovieApi(apiKeyInterceptor: Interceptor): MovieDbApi
    {
        var httpClient = OkHttpClient()
        httpClient = httpClient.newBuilder().addInterceptor(apiKeyInterceptor).build()
        val api = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/").addConverterFactory(GsonConverterFactory.create()).client(
                httpClient).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build()

        return api.create(MovieDbApi::class.java)
    }

    @Provides fun provideApiKeyInterceptor(): Interceptor
    {
        return Interceptor { chain ->
            val request = chain.request()
            val url = request.url().newBuilder().addQueryParameter("api_key", "***REMOVED***").build()
            chain.proceed(request.newBuilder().url(url).build())
        }
    }

    @Provides fun context(): Context
    {
        return application
    }

    @Provides fun preferences(context: Context): SharedPreferences
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides fun screenWidth(context: Context): Int
    {
        return context.resources.displayMetrics.widthPixels
    }
}