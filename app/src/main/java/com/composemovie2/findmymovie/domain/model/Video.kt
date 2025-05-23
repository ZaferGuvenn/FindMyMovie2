package com.composemovie2.findmymovie.domain.model

data class Video(
    val id: String,
    val key: String,      // e.g., YouTube key
    val name: String,
    val site: String,     // e.g., "YouTube"
    val type: String,     // e.g., "Trailer"
    val thumbnailUrl: String // Full URL to a thumbnail if possible
)
