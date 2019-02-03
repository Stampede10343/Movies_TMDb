package com.dev.cameronc.movies.options

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.dev.cameronc.androidutilities.BaseKey
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.movies.BuildConfig
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.License
import com.marcoscg.licenser.LicenserDialog
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.options_screen.view.*

class OptionsScreen : BaseScreen {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        if (!isInEditMode) MoviesApp.activityComponent.inject(this)
    }

    override fun viewReady() {
        options_toolbar.setNavigationOnClickListener { Navigator.getBackstack(context).goBack() }
        options_toolbar.navigationContentDescription = "Go back"
        change_theme.setOnClickListener {
            if (activity.supportFragmentManager.findFragmentByTag("themePicker") == null) {
                ThemePickerFragment().show(activity.supportFragmentManager, "themePicker")
            }
        }

        third_party_libraries.setOnClickListener {
            LicenserDialog(activity)
                    .setTitle("Licenses")
                    .setLibrary(Library("Android Support Libraries", "https://developer.android.com/topic/libraries/support-library/index.html", License.APACHE))
                    .setLibrary(Library("Licenser", "https://github.com/marcoscgdev/Licenser", License.MIT))
                    .setLibrary(Library("Retrofit", "https://github.com/square/retrofit", License.APACHE))
                    .setLibrary(Library("okhttp", "https://github.com/square/okhttp", License.APACHE))
                    .setLibrary(Library("gson", "https://github.com/google/gson", License.APACHE))
                    .setLibrary(Library("Dagger 2", "https://github.com/google/dagger", License.APACHE))
                    .setLibrary(Library("Mockito", "https://github.com/mockito/mockito", License.MIT))
                    .setLibrary(Library("Glide", "https://github.com/bumptech/glide", License.APACHE))
                    .setLibrary(Library("RxJava", "https://github.com/ReactiveX/RxJava", License.APACHE))
                    .setLibrary(Library("joda-time-android", "https://github.com/dlew/joda-time-android", License.APACHE))
                    .setLibrary(Library("Simple Stack", "https://github.com/Zhuinden/simple-stack", License.APACHE))
                    .setLibrary(Library("PhotoView", "https://github.com/chrisbanes/PhotoView", License.APACHE))
                    .setLibrary(Library("Process Phoenix", "https://github.com/JakeWharton/ProcessPhoenix", License.APACHE))
                    .show()
        }

        if (BuildConfig.DEBUG) {
            toggle_app_component.visibility = View.VISIBLE
            toggle_app_component.setOnClickListener {
                MoviesApp.app.swapComponent()
                activity.recreate()
            }
        }
    }

    override fun getScreenName(): String = "Options"

    @Parcelize
    class Key : BaseKey(), Parcelable {
        override fun layout(): Int = R.layout.options_screen
    }
}