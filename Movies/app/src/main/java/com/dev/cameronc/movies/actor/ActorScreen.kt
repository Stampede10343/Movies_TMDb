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
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.appComponent
import com.dev.cameronc.movies.moviedetail.MovieDetailScreen
import com.dev.cameronc.movies.toDp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_actor.view.*
import timber.log.Timber
import javax.inject.Inject

class ActorScreen : BaseScreen {

    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var dateFormatter: DateFormatter
    @Inject
    lateinit var actorRoleAdapter: ActorMovieRoleAdapter
    @Inject
    lateinit var actorViewModel: ActorViewModel

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        if (!isInEditMode) context.appComponent().inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return

        actorRoleAdapter.movieRoleClickListener = {
            Navigator.getBackstack(context).goTo(MovieDetailScreen.MovieDetailKey(it))
        }

        val tmdbActorId = Backstack.getKey<ActorScreenKey>(context).actorId
        actorViewModel.getActorDetails(tmdbActorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ actorDetails ->
                    TransitionManager.beginDelayedTransition(actor_profile_name.parent as ViewGroup, Fade())
                    actor_profile_name.text = actorDetails.name
                    if (actorDetails.birthday.isNullOrBlank()) {
                        actor_profile_birthdate.visibility = View.GONE
                    } else {
                        actor_profile_birthdate.text = context.getString(R.string.actor_born, dateFormatter.formatDateToLongFormat(actorDetails.birthday!!))
                        if (actorDetails.deathDay == null) {
                            actor_profile_age.text = dateFormatter.getTimeSpanFromNow(actorDetails.birthday)
                        } else actor_profile_age.text = dateFormatter.getTimeSpanAtTime(actorDetails.birthday, actorDetails.deathDay)
                    }
                    if (actorDetails.deathDay.isNullOrBlank()) {
                        actor_profile_deathday.visibility = View.GONE
                    } else {
                        actor_profile_deathday.visibility = View.VISIBLE
                        actor_profile_deathday.text = context.getString(R.string.actor_died, dateFormatter.formatDateToLongFormat(actorDetails.deathDay!!))
                    }

                    actor_profile_place_of_birth.text = actorDetails.placeOfBirth
                    actor_profile_description.text = actorDetails.biography
                    imageDownloader.load(actorDetails.profilePhotoPath, actor_profile_image)
                            .apply(RequestOptions.centerCropTransform().placeholder(R.drawable.empty_profile))
                            .into(actor_profile_image)
                }, { error -> Timber.e(error) }).disposeBy(this)

        actorViewModel.getActorMovieCredits(tmdbActorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movieCredits ->
                    val margin = 8.toDp()
                    actor_movie_credits.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                    actor_movie_credits.layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
                    actorRoleAdapter.items = movieCredits.roles
                    actor_movie_credits.adapter = actorRoleAdapter
                }, { error -> Timber.e(error) }).disposeBy(this)
    }

    @Parcelize
    data class ActorScreenKey(val actorId: Long) : StateKey, Parcelable {
        override fun layout(): Int = R.layout.activity_actor
        override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
    }
}