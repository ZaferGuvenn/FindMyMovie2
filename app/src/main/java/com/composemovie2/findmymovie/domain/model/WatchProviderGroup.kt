package com.composemovie2.findmymovie.domain.model

data class WatchProviderGroup(
    val type: String, // e.g., "Stream", "Rent", "Buy"
    val providers: List<WatchProvider>,
    val tmdbLink: String? // Optional: Link to TMDB's where to watch page for the region
)
