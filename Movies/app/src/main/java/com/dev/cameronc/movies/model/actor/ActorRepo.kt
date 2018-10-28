package com.dev.cameronc.movies.model.actor

import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.actor.ActorCreditsResponse
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActorRepo @Inject constructor(private val movieDbApi: MovieDbApi, boxStore: BoxStore, private val actorMapper: ActorMapper) {
    private val actorCache: MutableMap<Long, ActorDetails> = emptyMap<Long, ActorDetails>().toMutableMap()
    private val actorBox = boxStore.boxFor(ActorDetails::class.java)
    private val actorDetailsSubject = BehaviorSubject.create<ActorDetails>()
    private val actorIdSubject = BehaviorSubject.create<Long>()
    private val subscriptions = CompositeDisposable()

    init {
        subscriptions.add(actorIdSubject
                .flatMap { actorId ->
                    val remote = movieDbApi.actorDetails(actorId)
                            .subscribeOn(Schedulers.io())
                            .toObservable()
                            .map { actorMapper.mapToAppActor(it) }
                            .doOnNext { actor -> actorCache[actor.tmdbId] = actor }
                            .doOnNext { actor -> actorBox.put(actor) }

                    val cache = if (actorCache[actorId] != null) Observable.just<ActorDetails>(actorCache[actorId])
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread()) else Observable.empty()
                    val actorDetails = actorBox.get(actorId)
                    val disk = if (actorDetails != null) Observable.just(actorDetails)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread()) else Observable.empty()

                    return@flatMap Observable.concat(cache, disk, remote)
                            .firstElement()
                            .toObservable()
                }.subscribe { actorDetailsSubject.onNext(it) })
    }

    fun getActorDetails(tmdbActorId: Long): Observable<ActorDetails> {
        actorIdSubject.onNext(tmdbActorId)
        return actorDetailsSubject
    }

    fun getActorMovieCredits(tmdbActorId: Long): Observable<ActorCreditsResponse> =
            movieDbApi.actorMovieCredits(tmdbActorId).toObservable()

}