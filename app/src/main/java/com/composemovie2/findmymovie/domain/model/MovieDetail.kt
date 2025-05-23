package com.composemovie2.findmymovie.domain.model

// Import new supporting models
// No imports needed if they are in the same package, which they are.

data class MovieDetail(
    // Existing fields (some might be re-evaluated based on TMDB data)
    val title: String,
    val year: String, // Extracted from release_date
    val poster: String, // Full URL for poster
    val released: String, // Release date
    val imdbRating: String, // TMDB vote_average (0.0-10.0) as String
    val language: String, // Original language code (e.g., "en")
    val overview: String,

    // Fields that were previously often empty strings, now to be properly populated
    val actors: String, // This will be deprecated/removed in favor of 'cast' list
    val awards: String, // This might be hard to get directly from TMDB movie details
    val country: String, // This will be deprecated/removed (TMDB provides list of production_countries)
    val director: String, // This will be deprecated/removed in favor of 'crew' list (Director job)
    
    // New or updated fields for richer detail
    val backdropPath: String?, // Full URL for backdrop
    val runtime: Int?, // In minutes
    val tagline: String?,
    val status: String?, // e.g., "Released"
    val voteCount: Int?,
    val genresList: List<String>, // List of genre names
    
    val cast: List<CastMember>,
    val crew: List<CrewMember>, // Filtered for key roles like Director, Writer
    val videos: List<Video> // Filtered for official trailers/teasers on YouTube
)
