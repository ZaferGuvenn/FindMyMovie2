package com.composemovie2.findmymovie.domain.model

data class WatchProvider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String, // Full URL to the logo
    val displayPriority: Int
)
