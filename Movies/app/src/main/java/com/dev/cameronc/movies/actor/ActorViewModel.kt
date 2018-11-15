package com.dev.cameronc.movies.actor

import android.arch.lifecycle.ViewModel
import com.dev.cameronc.movies.model.actor.ActorCredits
import com.dev.cameronc.movies.model.actor.ActorDetails
import com.dev.cameronc.movies.model.actor.ActorRepo
import com.dev.cameronc.movies.model.actor.ActorScreenModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class ActorViewModel @Inject constructor(private val actorRepo: ActorRepo) : ViewModel() {

    fun getScreenModel(tmdbActorId: Long): Observable<ActorScreenModel> {
        return Observable.zip(getActorDetails(tmdbActorId), getActorMovieCredits(tmdbActorId), BiFunction { actorDetails, credits ->
            ActorScreenModel(actorDetails.name, actorDetails.birthday, actorDetails.deathDay, "25",
                    actorDetails.placeOfBirth, actorDetails.biography, actorDetails.profilePhotoPath, credits.roles)
        })
    }

    private fun getActorDetails(tmdbActorId: Long): Observable<ActorDetails> = actorRepo.getActorDetails(tmdbActorId)

    private fun getActorMovieCredits(tmdbActorId: Long): Observable<ActorCredits> =
            actorRepo.getActorMovieCredits(tmdbActorId)
                    .map { response ->
                        ActorCredits(response.cast
                                .asSequence()
                                .toMutableList()
                                .asSequence()
                                .filter { !it.posterPath.isNullOrBlank() }
                                .sortedByDescending { it.popularity }
                                .toList())
                    }
}