package com.dev.cameronc.movies.model

import android.content.SharedPreferences
import com.dev.cameronc.androidutilities.AnalyticTrackingHelper
import com.dev.cameronc.movies.model.movie.UpcomingMovie
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

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
}