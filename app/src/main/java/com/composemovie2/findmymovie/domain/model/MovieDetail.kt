package com.composemovie2.findmymovie.domain.model

// Imports for Video, CastMember, CrewMember, WatchProviderGroup are not strictly needed
// if they are in the same package, which they are.

data class MovieDetail(
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
    val imdbId: String?,
    val budget: Long?,
    val revenue: Long?,
    val tagline: String?,
    val year: String?
)
