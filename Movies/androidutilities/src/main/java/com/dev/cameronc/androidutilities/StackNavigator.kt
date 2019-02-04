package com.dev.cameronc.androidutilities

import com.zhuinden.simplestack.Backstack
import javax.inject.Inject

class StackNavigator @Inject constructor(private val backstack: Backstack) : AppNavigator {

    override fun goToScreen(screenKey: Any) {
        backstack.goTo(screenKey)
    }

    override fun goBack() {
        backstack.goBack()
    }

    override fun goUpToScreen(screenKey: Any) {
        backstack.goUp(screenKey)
    }
}