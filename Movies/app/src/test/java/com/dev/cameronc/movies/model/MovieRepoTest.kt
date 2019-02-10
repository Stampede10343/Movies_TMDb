package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.moviedb.data.movie.MovieResponseItem
import com.dev.cameronc.moviedb.data.movie.detail.MovieCreditsResponse
import com.dev.cameronc.moviedb.data.movie.detail.MovieDetailsResponse
import com.dev.cameronc.moviedb.data.movie.detail.ReleaseDates
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMoviesResponse
import com.dev.cameronc.movies.model.movie.MovieReview
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class MovieRepoTest {
    private lateinit var movieRepo: MovieRepo
    @Mock
    private lateinit var networkDataSource: MovieDataSource
    @Mock
    private lateinit var diskDataSource: MovieDataSource
    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        movieRepo = MovieRepo(networkDataSource, diskDataSource, mock(AnalyticTrackingHelper::class.java), sharedPreferences)
    }

    @Test
    fun testNothingIsEmittedWhenDataSourcesDoNotReturnAnything() {
        `when`(networkDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.empty())
        `when`(diskDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.empty())

        val testObserver = TestObserver.create<List<UpcomingMovie>>()
        movieRepo.getUpcomingMovies("").subscribe(testObserver)

        testObserver.assertNoValues()
    }

    @Test
    fun testNetworkDataSourceDoesNotGetCalledUntilDiskEmitsSomethingWhenResultsHaveBeenSavedRecently() {
        val networkMovies = emptyList<UpcomingMovie>()
        `when`(networkDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.just(networkMovies))
        `when`(diskDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.never())
        `when`(sharedPreferences.getLong("movie_save_time", Long.MAX_VALUE)).thenReturn(System.currentTimeMillis())


        val testObserver = TestObserver.create<List<UpcomingMovie>>()
        movieRepo.getUpcomingMovies("").subscribe(testObserver)

        testObserver.assertNoValues()
    }

    @Test
    fun testNetworkDataSourceDoesNotEmitUntilDiskDataSourceCompletes() {
        `when`(networkDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.just(emptyList()))
        val diskMovies = emptyList<UpcomingMovie>()
        val doesNotComplete = Observable.never<List<UpcomingMovie>>().startWith(diskMovies)
        `when`(diskDataSource.getUpcomingMovies(anyString())).thenReturn(doesNotComplete)
        `when`(sharedPreferences.getLong(eq("movie_save_time"), anyLong())).thenReturn(System.currentTimeMillis())


        val testObserver = TestObserver.create<List<UpcomingMovie>>()
        movieRepo.getUpcomingMovies("").subscribe(testObserver)

        testObserver.assertValue(diskMovies)
    }

    @Test
    fun testDiskDataSourceSkippedIfDataIsOlderThanOneDayOld() {
        val networkMovies = listOf(UpcomingMovie(0, "network", false, "network", "network", "network", "network", "network", "", 0f))
        `when`(networkDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.just(networkMovies))

        val diskMovies = listOf(UpcomingMovie(1, "disk", false, "disk", "disk", "disk", "disk", "disk", "", 0f))
        `when`(diskDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.just(diskMovies))

        val twoDaysAgo = DateTime.now().minus(Duration.standardDays(2)).millis
        `when`(sharedPreferences.getLong(eq("movie_save_time"), anyLong())).thenReturn(twoDaysAgo)

        val testObserver = TestObserver.create<List<UpcomingMovie>>()
        movieRepo.getUpcomingMovies("").subscribe(testObserver)

        testObserver.assertValue(networkMovies)
    }

    @Test
    fun testDiskDataSourceUsedWhenDataIsLessThanOneDayOld() {
        val networkMovies = listOf(UpcomingMovie(0, "network", false, "network", "network", "network", "network", "network", "", 0f))
        `when`(networkDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.just(networkMovies))

        val diskMovies = listOf(UpcomingMovie(1, "disk", false, "disk", "disk", "disk", "disk", "disk", "", 0f))
        `when`(diskDataSource.getUpcomingMovies(anyString())).thenReturn(Observable.just(diskMovies))

        val twentyThreeHoursAgo = DateTime.now().minus(Duration.standardHours(23)).millis
        `when`(sharedPreferences.getLong(eq("movie_save_time"), anyLong())).thenReturn(twentyThreeHoursAgo)

        val testObserver = TestObserver.create<List<UpcomingMovie>>()
        movieRepo.getUpcomingMovies("").subscribe(testObserver)

        val allMovies = diskMovies.toMutableList()
        allMovies.addAll(networkMovies)
        testObserver.assertValue(allMovies)
    }

    @Test
    fun testMovieDetailsGetCachedAfterNetworkResponse() {
        val movieDetailsResponse = MovieDetailsResponse(false, "", false, 0, emptyList(), "", 1, "1", "en", "title", "overview", 1.0, "", emptyList(), emptyList(), "", ReleaseDates(emptyList()), 1L, 90, emptyList(), "", "", "", false, 1.0, 1)
        val testScheduler = TestScheduler()
        `when`(networkDataSource.getMovieDetails(0)).thenReturn(Observable.just(movieDetailsResponse).delay(100, TimeUnit.MILLISECONDS, testScheduler))

        val testObserver = TestObserver.create<MovieDetailsResponse>()
        movieRepo.getMovieDetails(0).subscribe(testObserver)

        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(movieDetailsResponse)

        val testObserver2 = TestObserver.create<MovieDetailsResponse>()
        movieRepo.getMovieDetails(0).subscribe(testObserver2)
        testObserver2.assertValue(movieDetailsResponse)
    }

    @Test
    fun testMovieCreditsGetCachedAfterNetworkResponse() {
        val testScheduler = TestScheduler()
        val movieCreditsResponse = MovieCreditsResponse(emptyList(), emptyList(), 1)
        `when`(networkDataSource.getMovieCredits(1)).thenReturn(Observable.just(movieCreditsResponse).delay(1, TimeUnit.SECONDS, testScheduler))

        val testObserver = TestObserver.create<MovieCreditsResponse>()
        movieRepo.getMovieCredits(1).subscribe(testObserver)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertValue(movieCreditsResponse)

        val testObserver2 = TestObserver.create<MovieCreditsResponse>()
        movieRepo.getMovieCredits(1).subscribe(testObserver2)
        testObserver2.assertValue(movieCreditsResponse)
    }

    @Test
    fun testSimilarMoviesGetCachedAfterNetworkResponse() {
        val testScheduler = TestScheduler()
        val similarMovies = SimilarMoviesResponse(1, emptyList(), 2, 50)
        `when`(networkDataSource.getSimilarMovies(1)).thenReturn(Observable.just(similarMovies).delay(1, TimeUnit.SECONDS, testScheduler))

        val testObserver = TestObserver.create<SimilarMoviesResponse>()
        movieRepo.getSimilarMovies(1).subscribe(testObserver)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertValue(similarMovies)

        val testObserver2 = TestObserver.create<SimilarMoviesResponse>()
        movieRepo.getSimilarMovies(1).subscribe(testObserver2)
        testObserver2.assertValue(similarMovies)
    }

    @Test
    fun testMovieReviewsGetCachedAfterNetworkResponse() {
        val testScheduler = TestScheduler()
        val movieReviews = emptyList<MovieReview>()
        `when`(networkDataSource.getMovieReviews(1)).thenReturn(Observable.just(movieReviews).delay(1, TimeUnit.SECONDS, testScheduler))

        val testObserver = TestObserver.create<List<MovieReview>>()
        movieRepo.getMovieReviews(1).subscribe(testObserver)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertValue(movieReviews)

        val testObserver2 = TestObserver.create<List<MovieReview>>()
        movieRepo.getMovieReviews(1).subscribe(testObserver2)
        testObserver2.assertValue(movieReviews)
    }

    @Test
    fun testSaveMoviesDelegatesToTheDiskDataSourceToSave() {
        val movies = listOf(MovieResponseItem("path", false, "overview", "", 1, 1, "Title", "en", "title", null, 1f, 1, false, 1f))
        movieRepo.saveMovies(movies)

        verify(diskDataSource).saveMovies(movies)
    }

    @Test
    fun testSaveMoviesDoesNotDelegateToNetworkDataSource() {
        val movies = listOf(MovieResponseItem("path", false, "overview", "", 1, 1, "Title", "en", "title", null, 1f, 1, false, 1f))
        movieRepo.saveMovies(movies)

        verify(networkDataSource, never()).saveMovies(movies)
    }
}