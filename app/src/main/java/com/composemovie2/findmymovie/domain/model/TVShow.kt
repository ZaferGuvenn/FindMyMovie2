package com.composemovie2.findmymovie.domain.model

data class TVShow(
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val genres: List<String>?,
    val status: String?,
    val numberOfSeasons: Int?,
    val numberOfEpisodes: Int?
) 