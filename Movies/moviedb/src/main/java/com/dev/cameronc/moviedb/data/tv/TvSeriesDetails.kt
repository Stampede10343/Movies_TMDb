package com.dev.cameronc.moviedb.data.tv
import com.dev.cameronc.moviedb.data.movie.detail.Genre
import com.google.gson.annotations.SerializedName

data class TvSeriesDetails(
        @SerializedName("backdrop_path")
    val backdropPath: String?,
        @SerializedName("created_by")
    val createdBy: List<CreatedBy>,
        @SerializedName("episode_run_time")
    val episodeRunTime: List<Int>,
        @SerializedName("first_air_date")
    val firstAirDate: String,
        @SerializedName("genres")
    val genres: List<Genre>,
        @SerializedName("homepage")
    val homepage: String,
        @SerializedName("id")
    val id: Long,
        @SerializedName("in_production")
    val inProduction: Boolean,
        @SerializedName("languages")
    val languages: List<String>,
        @SerializedName("last_air_date")
    val lastAirDate: String,
        @SerializedName("last_episode_to_air")
    val lastEpisodeToAir: LastEpisodeToAir,
        @SerializedName("name")
    val name: String,
        @SerializedName("networks")
    val networks: List<Network>,
        @SerializedName("next_episode_to_air")
    val nextEpisodeToAir: Any?,
        @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int,
        @SerializedName("number_of_seasons")
    val numberOfSeasons: Int,
        @SerializedName("origin_country")
    val originCountry: List<String>,
        @SerializedName("original_language")
    val originalLanguage: String,
        @SerializedName("original_name")
    val originalName: String,
        @SerializedName("overview")
    val overview: String,
        @SerializedName("popularity")
    val popularity: Double,
        @SerializedName("poster_path")
    val posterPath: String?,
        @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>,
        @SerializedName("seasons")
    val seasons: List<Season>,
        @SerializedName("status")
    val status: String,
        @SerializedName("type")
    val type: String,
        @SerializedName("vote_average")
    val voteAverage: Double,
        @SerializedName("vote_count")
    val voteCount: Int
)

data class Season(
    @SerializedName("air_date")
    val airDate: String,
    @SerializedName("episode_count")
    val episodeCount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("season_number")
    val seasonNumber: Int
)

data class ProductionCompany(
    @SerializedName("id")
    val id: Int,
    @SerializedName("logo_path")
    val logoPath: Any?,
    @SerializedName("name")
    val name: String,
    @SerializedName("origin_country")
    val originCountry: String
)

data class CreatedBy(
    @SerializedName("credit_id")
    val creditId: String,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String
)

data class Network(
    @SerializedName("id")
    val id: Int,
    @SerializedName("logo_path")
    val logoPath: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("origin_country")
    val originCountry: String
)

data class LastEpisodeToAir(
    @SerializedName("air_date")
    val airDate: String,
    @SerializedName("episode_number")
    val episodeNumber: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("production_code")
    val productionCode: String,
    @SerializedName("season_number")
    val seasonNumber: Int,
    @SerializedName("show_id")
    val showId: Int,
    @SerializedName("still_path")
    val stillPath: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)