package com.dev.cameronc.androidutilities.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObservableConnectivityManager @Inject constructor(private val connectivityManager: ConnectivityManager) : BroadcastReceiver() {
    private val connectivitySubject: Subject<Boolean> = BehaviorSubject.create()

    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = connectivityManager.activeNetworkInfo?.isConnected == true
        connectivitySubject.onNext(isConnected)
    }

    fun connectivityAvailable(): Observable<Boolean> = connectivitySubject.debounce(2, TimeUnit.SECONDS).filter { it }

    fun connectivityChanged(): Observable<Boolean> = connectivitySubject
            .debounce(1, TimeUnit.SECONDS)
            .flatMap { isConnected ->
                if (isConnected) Observable.just(isConnected).delay(2, TimeUnit.SECONDS) else Observable.just(isConnected)
            }

}