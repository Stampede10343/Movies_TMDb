package com.dev.cameronc.movies.actor

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.transition.Fade
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.dev.cameronc.androidutilities.DateFormatter
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.model.actor.ActorScreenModel
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.toDp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.statebundle.StateBundle
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.actor_screen.view.*
import timber.log.Timber
import javax.inject.Inject

class ActorScreen : BaseScreen, Bundleable {

    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var dateFormatter: DateFormatter
    @Inject
    lateinit var actorRoleAdapter: ActorMovieRoleAdapter
    @Inject
    lateinit var actorViewModel: ActorViewModel

    private var previousScrollPosition: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        if (!isInEditMode) MoviesApp.activityComponent.inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return

        val tmdbActorId = Backstack.getKey<ActorScreenKey>(context).actorId
        actorViewModel.getScreenModel(tmdbActorId)
                .subscribe({ actorDetails ->
                    TransitionManager.beginDelayedTransition(actor_profile_name.parent as ViewGroup, Fade())
                    setMainActorInfo(actorDetails)
                    setActorBirthDetails(actorDetails)
                    setupRolesList(actorDetails)
                }, { error -> Timber.e(error) }).disposeBy(this)

    }

    private fun setMainActorInfo(actorDetails: ActorScreenModel) {
        actor_profile_name.text = actorDetails.name
        actor_profile_description.text = actorDetails.biography
        imageDownloader.load(actorDetails.profileImagePath, actor_profile_image)
                .apply(RequestOptions.centerCropTransform().placeholder(R.drawable.empty_profile))
                .into(actor_profile_image)
    }

    private fun setActorBirthDetails(actorDetails: ActorScreenModel) {
        if (actorDetails.birthday.isNullOrBlank()) {
            actor_profile_birthdate.visibility = View.GONE
        } else {
            actor_profile_birthdate.text = context.getString(R.string.actor_born, dateFormatter.formatDateToLongFormat(actorDetails.birthday))
            if (actorDetails.deathDay == null) {
                actor_profile_age.text = dateFormatter.getTimeSpanFromNow(actorDetails.birthday)
            } else {
                actor_profile_age.text = dateFormatter.getTimeSpanAtTime(actorDetails.birthday, actorDetails.deathDay)
            }
        }

        if (actorDetails.deathDay.isNullOrBlank()) {
            actor_profile_deathday.visibility = View.GONE
        } else {
            actor_profile_deathday.visibility = View.VISIBLE
            actor_profile_deathday.text = context.getString(R.string.actor_died, dateFormatter.formatDateToLongFormat(actorDetails.deathDay))
        }
        actor_profile_place_of_birth.text = actorDetails.placeOfBirth
    }

    private fun setupRolesList(actorDetails: ActorScreenModel) {
        val margin = 8.toDp()
        actor_movie_credits.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
        actor_movie_credits.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        actorRoleAdapter.items = actorDetails.actorRoles
        actorRoleAdapter.movieRoleClickListener = {
            Navigator.getBackstack(context).goTo(MovieDetailScreen.MovieDetailKey(it))
        }
        actor_movie_credits.adapter = actorRoleAdapter
    }

    override fun getScreenName(): String = "Actor"

    override fun toBundle(): StateBundle = StateBundle().apply { putInt("scrollY", actor_scrollview.scrollY) }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.apply { previousScrollPosition = getInt("scrollY", 0) }
    }

    @Parcelize
    data class ActorScreenKey(val actorId: Long) : StateKey, Parcelable {
        override fun layout(): Int = R.layout.actor_screen
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}