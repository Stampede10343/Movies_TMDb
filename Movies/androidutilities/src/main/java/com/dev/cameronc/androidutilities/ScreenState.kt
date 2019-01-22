package com.dev.cameronc.androidutilities

@Suppress("unused")
sealed class ScreenState<out T> {
    class Loading<out T> : ScreenState<T>() {
        override fun equals(other: Any?): Boolean = if (other is Loading<*>) true else super.equals(other)
        override fun hashCode(): Int = 1
    }

    data class Ready<out T>(val data: T) : ScreenState<T>()
    // Error?
}