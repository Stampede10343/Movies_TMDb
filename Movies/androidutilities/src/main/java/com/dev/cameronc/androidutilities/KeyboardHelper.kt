package com.dev.cameronc.androidutilities

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import timber.log.Timber
import javax.inject.Inject


class KeyboardHelper @Inject constructor(private val activity: Activity) : ViewTreeObserver.OnGlobalLayoutListener {
    var keyboardOpened: (() -> Unit)? = null
    var keyboardClosed: (() -> Unit)? = null

    private var isOpened: Boolean = false

    override fun onGlobalLayout() {
        val activityRootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = activityRootView.rootView.height - activityRootView.height
            if (heightDiff > 300) { // 99% of the time the height diff will be due to a keyboard.
                // Opened
                Timber.v("Keyboard opened")
                keyboardOpened?.invoke()
                if (!isOpened) {

                }
                isOpened = true
            } else if (isOpened) {
                isOpened = false
                keyboardClosed?.invoke()
                Timber.v("Keyboard closed")
            }
        }
    }

    fun dismissKeyboard() {
        val imm: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.findViewById<View>(android.R.id.content).windowToken, 0)
    }
}