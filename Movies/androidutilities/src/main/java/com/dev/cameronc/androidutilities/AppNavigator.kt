package com.dev.cameronc.androidutilities

interface AppNavigator {
    fun goToScreen(screenKey: Any)
    fun goBack()
    fun goUpToScreen(screenKey: Any)
}