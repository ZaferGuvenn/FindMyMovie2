package com.composemovie2.findmymovie.domain.model


data class Movie(
    val poster: String,
    val title: String,
    val type: String,
    val imdbId: String,
    val year: String
)