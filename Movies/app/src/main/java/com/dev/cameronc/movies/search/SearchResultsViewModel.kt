package com.dev.cameronc.movies.search

import com.dev.cameronc.movies.ViewModel
import com.dev.cameronc.movies.model.MovieRepo
import com.dev.cameronc.movies.model.MultiSearchResult
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(private val movieRepo: MovieRepo) : ViewModel() {
    private val resultsSubject = BehaviorSubject.create<List<MultiSearchResult>>()
    private val querySubject = PublishSubject.create<String>()
    private val subscriptions = CompositeDisposable()

    init {
        subscriptions.add(querySubject
                .debounce(250, TimeUnit.MILLISECONDS)
                .flatMap { query ->
                    movieRepo.searchAll(query).subscribeOn(Schedulers.io()).map { it.results }
                }
                .map { allResults -> categorizeResults(allResults) }
                .subscribe({ resultsSubject.onNext(it) }, { error -> Timber.e(error) }))
    }

    private fun categorizeResults(allResults: List<com.dev.cameronc.moviedb.data.MultiSearchResult>): MutableList<MultiSearchResult> {
        val typedResults = emptyList<MultiSearchResult>().toMutableList()
        val moviesResults = allResults.filter { item -> item.mediaType == "movie" }
        moviesResults.forEach { typedResults.add(MultiSearchResult.MovieSearchResult(it.id, it.posterPath, it.title)) }
        val tvResults = allResults.filter { item -> item.mediaType == "tv" }
        tvResults.forEach { typedResults.add(MultiSearchResult.TelevisionSearchResult(it.id, it.posterPath, it.name)) }

        return typedResults
    }

    fun updateQuery(query: String?) {
        if (!query.isNullOrBlank()) querySubject.onNext(query)
    }

    fun results(): Observable<List<MultiSearchResult>> = resultsSubject

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }
}