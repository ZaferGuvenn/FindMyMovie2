package com.composemovie2.findmymovie.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val genres: List<Genre>?,
    val status: String?,
    val runtime: Int?,
    val imdbRating: Double?,
    val imdbVotes: Int?,
    val imdbId: String?,
    val budget: Long?,
    val revenue: Long?,
    val tagline: String?,
    val year: String?
)