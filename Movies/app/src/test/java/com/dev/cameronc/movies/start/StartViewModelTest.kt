package com.dev.cameronc.movies.start

import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.movies.Schedulers
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import com.dev.cameronc.movies.testhelpers.FakeMovieRepository
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class StartViewModelTest {
    private lateinit var viewModel: StartViewModel
    private lateinit var movieRepo: FakeMovieRepository
    private val testScheduler: TestScheduler = TestScheduler()

    @Before
    fun setUp() {
        movieRepo = FakeMovieRepository()
        viewModel = StartViewModel(movieRepo, Schedulers(testScheduler, testScheduler))
    }

    @Test
    fun testGetUpcomingMoviesStartsWithLoadingState() {
        val subscriber: TestObserver<ScreenState<MutableList<UpcomingMovie>>> = TestObserver()
        viewModel.getUpcomingMovies().subscribe(subscriber)

        subscriber.assertValueAt(0, ScreenState.Loading())
    }

    @Test
    fun testGetUpcomingMoviesHasEmptyEmissionAfterLoading() {
        val subscriber: TestObserver<ScreenState<MutableList<UpcomingMovie>>> = TestObserver()
        viewModel.getUpcomingMovies().subscribe(subscriber)

        subscriber.assertValueAt(1, ScreenState.Ready(emptyList<UpcomingMovie>().toMutableList()))
    }

    @Test
    fun testLoadMoreEmitsAdditionalItems() {
        val subscriber: TestObserver<ScreenState<MutableList<UpcomingMovie>>> = TestObserver()
        viewModel.getUpcomingMovies().subscribe(subscriber)
        viewModel.loadMoreMovies()

        subscriber.assertValueAt(2, ScreenState.Ready(emptyList<UpcomingMovie>().toMutableList()))
        subscriber.assertValueCount(3)
    }

    @Test
    fun testSearchResultsDoesNotEmitItems() {
        val subscriber = TestObserver<List<SearchResult>>()
        viewModel.searchResults().subscribe(subscriber)

        subscriber.assertNoValues()
        subscriber.assertNotComplete()
    }

    @Test
    fun testSearchResultsEmitsItemsAfterQueryIsUpdated() {
        val subscriber = TestObserver<List<SearchResult>>()
        viewModel.searchResults().subscribe(subscriber)

        viewModel.queryTextChanged("query")
        testScheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        subscriber.assertValueCount(1)
        subscriber.assertValue(emptyList())
    }

    @Test
    fun testSearchResultsDebouncesQuery() {
        val subscriber = TestObserver<List<SearchResult>>()
        viewModel.searchResults().subscribe(subscriber)

        viewModel.queryTextChanged("qu")
        testScheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS)
        viewModel.queryTextChanged("query")
        testScheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        subscriber.assertValueCount(1)
        subscriber.assertValue(emptyList())
    }

    @Test
    fun testUpcomingMoviesStopsEmittingAfterTheTenthPage() {
        val subscriber: TestObserver<ScreenState<MutableList<UpcomingMovie>>> = TestObserver()
        viewModel.getUpcomingMovies().subscribe(subscriber)
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()
        viewModel.loadMoreMovies()

        subscriber.assertValueCount(11)
    }

    @Test
    fun testUpcomingMovieRepoReturnsError() {
        movieRepo.upcomingMovies = Observable.error(Exception())

        val subscriber: TestObserver<ScreenState<MutableList<UpcomingMovie>>> = TestObserver()
        viewModel.getUpcomingMovies().subscribe(subscriber)

        subscriber.assertError(Exception::class.java)
    }
}