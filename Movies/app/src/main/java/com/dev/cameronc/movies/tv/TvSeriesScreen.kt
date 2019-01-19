package com.dev.cameronc.movies.tv

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.util.AttributeSet
import com.dev.cameronc.androidutilities.BaseKey
import com.dev.cameronc.androidutilities.DateFormatter
import com.dev.cameronc.androidutilities.view.BaseScreen
import com.dev.cameronc.androidutilities.view.MarginItemDecoration
import com.dev.cameronc.movies.MovieImageDownloader
import com.dev.cameronc.movies.MoviesApp
import com.dev.cameronc.movies.R
import com.dev.cameronc.movies.moviedetail.MovieGenreAdapter
import com.dev.cameronc.movies.toDp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.tv_series_screen.view.*
import javax.inject.Inject

class TvSeriesScreen : BaseScreen {

    @Inject
    lateinit var viewModel: TvSeriesViewModel
    @Inject
    lateinit var imageDownloader: MovieImageDownloader
    @Inject
    lateinit var dateFormatter: DateFormatter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        if (!isInEditMode) MoviesApp.activityComponent.inject(this)
    }

    override fun viewReady() {
        if (isInEditMode) return

        val seriesId = Backstack.getKey<Key>(context)
                .seriesId

        tv_series_toolbar.setNavigationOnClickListener {
            Navigator.getBackstack(context)
                    .goBack()
        }

        viewModel.getSeriesInfo(seriesId)
                .subscribe { model ->
                    tv_series_title.text = model.name + ' ' + dateFormatter.getYearSpan(model.firstAirDate, model.lastAirDate)
                    tv_series_description.text = model.description
                    val margin = 8.toDp()
                    tv_series_genres.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                    tv_series_genres.adapter = MovieGenreAdapter(model.genres)
                    tv_series_seasons.text = "${model.seasons.size} seasons"

                    imageDownloader.load(model.backdropPath, tv_series_backdrop)
                            .into(tv_series_backdrop)

                    tv_series_seasons_posters.adapter = SeasonTvPosterAdapter(model.seasons, imageDownloader)
                    tv_series_seasons_posters.addItemDecoration(MarginItemDecoration(Rect(margin, margin, margin, margin)))
                }
                .disposeBy(this)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel.onDestroy()
    }

    override fun getScreenName(): String = "TV Series: ${Backstack.getKey<Key>(context).seriesId}"

    @Parcelize
    class Key(val seriesId: Long) : BaseKey(), Parcelable {
        override fun layout(): Int = R.layout.tv_series_screen
    }
}