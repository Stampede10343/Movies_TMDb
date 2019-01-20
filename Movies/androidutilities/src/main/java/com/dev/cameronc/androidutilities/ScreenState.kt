package com.dev.cameronc.androidutilities

@Suppress("unused")
sealed class ScreenState<out T> {
    class Loading<out T> : ScreenState<T>()
    data class Ready<out T>(val data: T) : ScreenState<T>()
    // Error?
}