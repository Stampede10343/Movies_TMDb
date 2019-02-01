package com.dev.cameronc.movies.model.movie

import com.dev.cameronc.moviedb.data.movie.detail.Cast
import com.dev.cameronc.moviedb.data.movie.detail.SimilarMovie

data class MovieDetails(val id: Long,
                        val title: String,
                        val overview: String,
                        val releaseDate: String,
                        val runtime: Int,
                        val rating: String,
                        val ratingVisibility: Int,
                        val backdropPath: String?,
                        val genres: List<String>,
                        val voteAverage: String,
                        val voteAverageVisibility: Int,
                        val cast: List<Cast>,
                        val similarMovies: List<SimilarMovie>,
                        val reviews: List<MovieReview>)