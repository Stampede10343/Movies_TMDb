package com.dev.cameronc.movies.actor

import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.moviedb.data.tv.TvCast
import com.dev.cameronc.movies.ViewModel
import com.dev.cameronc.movies.model.actor.ActorCredits
import com.dev.cameronc.movies.model.actor.ActorDetails
import com.dev.cameronc.movies.model.actor.ActorRepo
import com.dev.cameronc.movies.model.actor.ActorScreenModel
import io.reactivex.Observable
import io.reactivex.functions.Function3
import javax.inject.Inject

class ActorViewModel @Inject constructor(private val actorRepo: ActorRepo) : ViewModel() {

    fun getScreenModel(tmdbActorId: Long): Observable<ScreenState<ActorScreenModel>> {
        return Observable.zip(getActorDetails(tmdbActorId), getActorMovieCredits(tmdbActorId), getActorTvCredits(),
                Function3<ActorDetails, ActorCredits, List<TvCast>, ScreenState<ActorScreenModel>> { actorDetails, movieCredits, tvCredits ->
                    ScreenState.Ready(ActorScreenModel(actorDetails.tmdbId, actorDetails.name, actorDetails.birthday,
                            actorDetails.deathDay, "25", actorDetails.placeOfBirth, actorDetails.biography,
                            actorDetails.profilePhotoPath, movieCredits.roles, tvCredits))
                })
                .startWith(ScreenState.Loading())
    }

    private fun getActorDetails(tmdbActorId: Long): Observable<ActorDetails> = actorRepo.getActorDetails(tmdbActorId)

    private fun getActorMovieCredits(tmdbActorId: Long): Observable<ActorCredits> =
            actorRepo.getActorMovieCredits(tmdbActorId)
                    .map { response ->
                        ActorCredits(response.cast
                                .asSequence()
                                .toMutableList()
                                .filter { !it.posterPath.isNullOrBlank() }
                                .sortedByDescending { it.popularity }
                                .toList())
                    }

    private fun getActorTvCredits(): Observable<List<TvCast>> =
            actorRepo.getActorTvCredits()
                    .map { tvCredits ->
                        tvCredits.toMutableList()
                                .asSequence()
                                .filter { !it.posterPath.isNullOrBlank() }
                                .sortedByDescending { it.popularity }
                                .toList()
                    }
}