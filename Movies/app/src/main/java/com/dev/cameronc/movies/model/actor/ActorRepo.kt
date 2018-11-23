package com.dev.cameronc.movies.model.actor

import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.moviedb.api.MovieDbApi
import com.dev.cameronc.moviedb.data.actor.ActorCreditsResponse
import com.dev.cameronc.moviedb.data.tv.TvCast
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActorRepo @Inject constructor(private val movieDbApi: MovieDbApi, boxStore: BoxStore, private val actorMapper: ActorMapper, private val analyticTracker: AnalyticTrackingHelper) {
    private val actorCache: MutableMap<Long, ActorDetails> = emptyMap<Long, ActorDetails>().toMutableMap()
    private val actorBox = boxStore.boxFor(ActorDetails::class.java)
    private val actorDetailsSubject = BehaviorSubject.create<ActorDetails>()
    private val actorIdSubject = BehaviorSubject.create<Long>()
    private val actorCreditCache: MutableMap<Long, ActorCreditsResponse> = emptyMap<Long, ActorCreditsResponse>().toMutableMap()
    private val actorCreditsSubject = BehaviorSubject.create<ActorCreditsResponse>()
    private val actorTvCreditCache: MutableMap<Long, List<TvCast>> = emptyMap<Long, List<TvCast>>().toMutableMap()
    private val actorTvCreditsSubject = BehaviorSubject.create<List<TvCast>>()
    private val subscriptions = CompositeDisposable()

    init {
        subscriptions.add(actorIdSubject
                .flatMap { actorId ->
                    val remote = movieDbApi.actorDetails(actorId)
                            .toObservable()
                            .map { actorMapper.mapToAppActor(it) }
                            .doOnNext { actor -> actorCache[actor.tmdbId] = actor }
                            .doOnNext { actor -> actorBox.put(actor) }
                            .doOnError { Timber.e("Error getting actor details! $it") }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())

                    val cache = if (actorCache[actorId] != null) Observable.just<ActorDetails>(actorCache[actorId]) else Observable.empty()
                    val actorDetails = actorBox.get(actorId)
                    val disk = if (actorDetails != null) Observable.just(actorDetails) else Observable.empty()

                    return@flatMap Observable.concat(cache, disk, remote)
                            .firstElement()
                            .toObservable()
                }
                .doOnNext { analyticTracker.trackEvent("Actor Details: ${it.tmdbId}") }
                .subscribe { actorDetailsSubject.onNext(it) })

        subscriptions.add(actorIdSubject
                .flatMap { actorId ->
                    val remote = movieDbApi.actorMovieCredits(actorId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .toObservable()
                            .doOnNext { actorCreditCache[actorId] = it }
                            .doOnNext { analyticTracker.trackEvent("Actor Credits: $actorId. Count: ${it.cast.size}") }
                            .doOnError { Timber.e("Error getting actor movie credits! $it") }

                    val cachedCredits = actorCreditCache[actorId]
                    val cache = if (cachedCredits == null) Observable.empty<ActorCreditsResponse>() else Observable.just(cachedCredits)

                    return@flatMap Observable.concat(cache, remote)
                            .firstElement()
                            .toObservable()
                }
                .subscribe { actorCreditsSubject.onNext(it) }
        )

        subscriptions.add(actorIdSubject.flatMap { actorId ->
            val remote = movieDbApi.actorTvCredits(actorId)
                    .toObservable()
                    .filter { it.isSuccessful }
                    .map { it.body()!! }
                    .map { it.tvCast }
                    .doOnNext { actorTvCreditCache[actorId] = it }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            val cache = if (actorTvCreditCache[actorId] == null) Observable.empty() else Observable.just<List<TvCast>>(actorTvCreditCache[actorId])

            return@flatMap Observable.concat(cache, remote)
                    .firstElement()
                    .toObservable()
        }.subscribe { actorTvCreditsSubject.onNext(it) })
    }

    fun getActorDetails(tmdbActorId: Long): Observable<ActorDetails> {
        actorIdSubject.onNext(tmdbActorId)
        return actorDetailsSubject
    }

    fun getActorMovieCredits(): Observable<ActorCreditsResponse> = actorCreditsSubject

    fun getActorTvCredits(): Observable<List<TvCast>> = actorTvCreditsSubject

}