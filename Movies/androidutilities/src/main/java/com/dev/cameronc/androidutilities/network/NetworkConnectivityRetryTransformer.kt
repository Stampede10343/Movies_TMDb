package com.dev.cameronc.androidutilities.network

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import java.net.UnknownHostException

class NetworkConnectivityRetryTransformer<T>(private val connectivityManager: ObservableConnectivityManager) : ObservableTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.retryWhen { exceptionObservable ->
            exceptionObservable.flatMap { error ->
                if (error is UnknownHostException) {
                    connectivityManager.connectivityAvailable()
                } else Observable.error(error)
            }
        }
    }

    companion object {
        fun <T> create(connectivityManager: ObservableConnectivityManager): NetworkConnectivityRetryTransformer<T> =
                NetworkConnectivityRetryTransformer(connectivityManager)
    }
}