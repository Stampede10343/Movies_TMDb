package com.dev.cameronc.androidutilities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import timber.log.Timber
import javax.inject.Inject


class KeyboardHelper @Inject constructor(private val activity: AppCompatActivity) {
    var keyboardOpened: (() -> Unit)? = null
    var keyboardClosed: (() -> Unit)? = null

    private var isOpened: Boolean = false
    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
        set(value) {
            val activityRootView = activity.window.decorView.findViewById<View>(android.R.id.content)
            activityRootView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
            field = value
        }

    fun dismissKeyboard() {
        val imm: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.findViewById<View>(android.R.id.content).windowToken, 0)
    }

    fun listenForKeyboard() {
        val activityRootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
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
            activityRootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        }
    }

    fun clearListener() {
        val activityRootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        activityRootView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
        layoutListener = null
    }
}