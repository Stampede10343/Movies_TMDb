package com.dev.cameronc.movies.actor

import android.arch.lifecycle.ViewModel
import com.dev.cameronc.movies.model.actor.ActorCredits
import com.dev.cameronc.movies.model.actor.ActorDetails
import com.dev.cameronc.movies.model.actor.ActorRepo
import io.reactivex.Observable
import javax.inject.Inject

class ActorViewModel @Inject constructor(private val actorRepo: ActorRepo) : ViewModel() {

    fun getActorDetails(tmdbActorId: Long): Observable<ActorDetails> = actorRepo.getActorDetails(tmdbActorId)
    fun getActorMovieCredits(tmdbActorId: Long): Observable<ActorCredits> =
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