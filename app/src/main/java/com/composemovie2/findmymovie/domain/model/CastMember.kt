package com.composemovie2.findmymovie.domain.model

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String? // Full URL
)
