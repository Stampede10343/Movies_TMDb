package com.dev.cameronc.androidutilities.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup

fun View.fadeAndRemove() {
    animate()
            .alphaBy(-1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    (parent as? ViewGroup)?.removeView(this@fadeAndRemove)
                }

                override fun onAnimationCancel(animation: Animator?) {
                    (parent as? ViewGroup)?.removeView(this@fadeAndRemove)
                }
            })
            .start()
}

fun View.fadeAndSetGone() {
    animate()
            .alphaBy(-1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    visibility = View.GONE
                }
            })
            .start()
}

fun View.fadeIn() {
    alpha = 0f
    animate()
            .alphaBy(1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    visibility = View.VISIBLE
                }
            })
            .start()
}