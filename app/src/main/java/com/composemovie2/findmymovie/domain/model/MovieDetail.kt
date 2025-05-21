package com.composemovie2.findmymovie.domain.model

import com.composemovie2.findmymovie.data.remote.dto.Rating
import kotlinx.serialization.SerialName

data class MovieDetail(
    @SerialName("Actors")
    val actors: String,
    @SerialName("Awards")
    val awards: String,

    @SerialName("Country")
    val country: String,

    @SerialName("Director")
    val director: String,

    @SerialName("imdbRating")
    val imdbRating: String,

    @SerialName("Language")
    val language: String,

    @SerialName("Poster")
    val poster: String,

    @SerialName("Released")
    val released: String,
    @SerialName("Title")
    val title: String,
    val year: String
)