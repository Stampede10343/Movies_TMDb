package com.dev.cameronc.movies.di.prod

import android.content.Context
import android.net.ConnectivityManager
import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.movie.detail.video.Site
import com.dev.cameronc.moviedb.data.movie.detail.video.SiteTransformer
import com.dev.cameronc.movies.BuildConfig
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideMovieApi(httpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory): MovieDbApi {
        val api = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(gsonConverterFactory)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return api.create(MovieDbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGsonConverter(): GsonConverterFactory {
        val gson = GsonBuilder()
                .registerTypeAdapter(Site::class.java, SiteTransformer())
                .create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @ApiInterceptor
    fun provideApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val url = request.url
                    .newBuilder()
                    .addQueryParameter("api_key", BuildConfig.API_KEY)
                    .build()
            chain.proceed(request.newBuilder().url(url).build())
        }
    }

    @Provides
    @Singleton
    fun httpCache(context: Context): Cache = Cache(File(context.cacheDir, "httpcache"), 20 * 1024 * 1024)

    @Provides
    fun provideHttpClient(@ApiInterceptor apiInterceptor: Interceptor,
                          @NetworkInterceptor connectivityInterceptor: Interceptor,
                          loggingInterceptor: HttpLoggingInterceptor,
                          cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(apiInterceptor)
                .addInterceptor(connectivityInterceptor)
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @NetworkInterceptor
    fun provideNetworkConnectivityInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity.isDefaultNetworkActive) {
                chain.proceed(chain.request())
            } else {
                val request = chain.request()
                        .newBuilder()
                        .addHeader("Cache-Control", "public, max-age=" + 60)
                        .build()
                chain.proceed(request)
            }
        }
    }

    @Provides
    fun loggingInterceptor(cache: Cache): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.v("$message\nCache Stats: ${cache.hitCount()}/${cache.requestCount()}")
            }
        })
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

}

@Qualifier
annotation class ApiInterceptor

@Qualifier
annotation class NetworkInterceptor
