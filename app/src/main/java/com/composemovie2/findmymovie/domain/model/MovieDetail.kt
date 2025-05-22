package com.composemovie2.findmymovie.domain.model


data class MovieDetail(
    val actors: String,
    val awards: String,

    val country: String,

    val director: String,

    val imdbRating: String,

    val language: String,

    val poster: String,

    val released: String,
    val title: String,
    val year: String
)