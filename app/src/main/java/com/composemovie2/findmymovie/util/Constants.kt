package com.composemovie2.findmymovie.util

object Constants {

    // OMDb (existing)
    const val OMDB_API_KEY = "77836e9f" // Renamed for clarity
    const val OMDB_API_URL = "http://omdbapi.com/" // Renamed for clarity

    // TMDB
    const val TMDB_API_BASE_URL = "https://api.themoviedb.org/3/"
    var TMDB_API_KEY = "1ac2ce195900a44c7586b61969156a2e" // Placeholder - To be filled by user input later
    var TMDB_IMAGE_BASE_URL = "" // Placeholder - To be fetched from TMDB configuration endpoint
    const val DEFAULT_POSTER_SIZE = "w500" // A common default poster size
    const val DEFAULT_BACKDROP_SIZE = "original" // For backdrop images
    const val DEFAULT_PROFILE_SIZE = "w185" // For profile images (cast/crew)
}