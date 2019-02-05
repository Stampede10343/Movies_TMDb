package com.dev.cameronc.movies.moviedetail

import com.dev.cameronc.androidutilities.ScreenState
import com.dev.cameronc.movies.model.movie.MovieDetails
import com.dev.cameronc.movies.start.FakeMovieRatingFinder
import com.dev.cameronc.movies.testhelpers.FakeMovieRepository
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import java.io.IOException

class MovieDetailViewModelTest {
    private lateinit var movieRepository: FakeMovieRepository
    private lateinit var viewModel: MovieDetailViewModel

    @Before
    fun setUp() {
        movieRepository = FakeMovieRepository()
        viewModel = MovieDetailViewModel(movieRepository, FakeMovieRatingFinder())
    }

    @Test
    fun testViewModelStartsWithLoadingStateWhenNothingHasLoadedYet() {
        movieRepository.movieDetails = Observable.never()

        val testObserver = TestObserver<ScreenState<MovieDetails>>()
        viewModel.movieDetails(0).subscribe(testObserver)

        testObserver.assertValue { it is ScreenState.Loading }
    }

    @Test
    fun testViewModelEmitsLoadingThenResultsWhenTheyAreReady() {
        val testObserver = TestObserver<ScreenState<MovieDetails>>()
        viewModel.movieDetails(0).subscribe(testObserver)

        testObserver.assertValueAt(0, ScreenState.Loading())
        testObserver.assertValueAt(1) { it is ScreenState.Ready<MovieDetails> }
        testObserver.assertValueCount(2)
    }

    @Test
    fun testRepoEmitsErrorViewModelPropagatesError() {
        movieRepository.movieDetails = Observable.error(IOException())

        val testObserver = TestObserver<ScreenState<MovieDetails>>()
        viewModel.movieDetails(0).subscribe(testObserver)

        testObserver.assertError(IOException::class.java)
    }

    @Test
    fun testViewModelLoadsMovieImagesWithoutLoadingState() {
        val testObserver = TestObserver<List<String>>()
        viewModel.movieImages(0).subscribe(testObserver)

        testObserver.assertValue { it.isEmpty() }
    }

    @Test
    fun testViewModelPropagatesMovieImageError() {
        movieRepository.movieImages = Observable.error(IOException())

        val testObserver = TestObserver<List<String>>()
        viewModel.movieImages(0).subscribe(testObserver)

        testObserver.assertError(IOException::class.java)
    }

}