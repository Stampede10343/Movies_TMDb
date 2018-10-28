package com.dev.cameronc.movies.model.actor

import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.actor.ActorCreditsResponse
import io.objectbox.BoxStore
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActorRepo @Inject constructor(private val movieDbApi: MovieDbApi, private val boxStore: BoxStore, private val actorMapper: ActorMapper) {

    fun getActorDetails(tmdbActorId: Long): Observable<ActorDetails> {
        return movieDbApi.actorDetails(tmdbActorId)
                .map { actorMapper.mapToAppActor(it) }
                .toObservable()
    }

    fun getActorMovieCredits(tmdbActorId: Long): Observable<ActorCreditsResponse> =
            movieDbApi.actorMovieCredits(tmdbActorId).toObservable()

}