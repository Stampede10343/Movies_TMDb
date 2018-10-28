package com.dev.cameronc.movies.model.actor

import io.reactivex.Observable

interface ActorRepository {
    fun actorDetails(actorId: Long): Observable<ActorDetails>
}