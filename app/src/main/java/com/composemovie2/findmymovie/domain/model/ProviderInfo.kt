package com.composemovie2.findmymovie.domain.model

data class ProviderInfo( // Represents a provider from the general list
    val providerId: Int,
    val providerName: String,
    val logoPath: String // Full URL
)
