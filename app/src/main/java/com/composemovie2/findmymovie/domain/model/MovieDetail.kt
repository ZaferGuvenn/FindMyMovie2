package com.composemovie2.findmymovie.domain.model

// Imports for Video, CastMember, CrewMember, WatchProviderGroup are not strictly needed
// if they are in the same package, which they are.

data class MovieDetail(
    // Existing fields from the last update
    val title: String,
    val year: String,
    val poster: String,
    val released: String,
    val imdbRating: String,
    val language: String,
    val overview: String,
    val actors: String, 
    val awards: String,
    val country: String,
    val director: String, 
    val backdropPath: String?,
    val runtime: Int?,
    val tagline: String?,
    val status: String?,
    val voteCount: Int?,
    val genresList: List<String>,
    val cast: List<CastMember>,
    val crew: List<CrewMember>,
    val videos: List<Video>,

    // New field for watch providers
    val watchProviderGroups: List<WatchProviderGroup>? // New
)
