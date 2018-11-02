package com.dev.cameronc.androidutilities

import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler

abstract class BaseKey : StateKey {
    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}