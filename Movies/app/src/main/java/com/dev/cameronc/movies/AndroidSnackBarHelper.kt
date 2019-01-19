package com.dev.cameronc.movies


import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dev.cameronc.androidutilities.SnackBarHelper
import com.dev.cameronc.androidutilities.network.ObservableConnectivityManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AndroidSnackBarHelper @Inject constructor(
        observableConnectivityManager: ObservableConnectivityManager,
        activity: AppCompatActivity) : SnackBarHelper, Application.ActivityLifecycleCallbacks {

    private val contentView: View = activity.findViewById(R.id.app_content)
    private val resources: Resources = activity.resources
    private var currentSnackBar: Snackbar? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        activity.application.registerActivityLifecycleCallbacks(this)

        compositeDisposable.add(observableConnectivityManager.connectivityChanged()
                .subscribe { isConnected ->
                    if (isConnected) {
                        dismissNetworkSnackBar()
                    } else {
                        showNetworkErrorSnackbar()
                    }
                })
    }

    override fun showNetworkErrorSnackbar() {
        if (currentSnackBar?.isShown != true) {
            currentSnackBar = Snackbar.make(contentView, resources.getString(R.string.connectivity_issue), Snackbar.LENGTH_INDEFINITE)
            currentSnackBar!!.show()
        }
    }

    override fun dismissNetworkSnackBar() {
        currentSnackBar?.dismiss()
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.application?.unregisterActivityLifecycleCallbacks(this)
        compositeDisposable.dispose()
    }

    override fun onActivityPaused(activity: Activity?) {}

    override fun onActivityResumed(activity: Activity?) {}

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

    override fun onActivityStarted(activity: Activity?) {}

    override fun onActivityStopped(activity: Activity?) {}
}