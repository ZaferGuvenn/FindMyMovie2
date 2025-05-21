package com.composemovie2.findmymovie.domain.model

import kotlinx.serialization.SerialName

data class Movie(
    @SerialName("Poster")
    val poster: String,
    @SerialName("Title")
    val title: String,
    @SerialName("Type")
    val type: String,
    @SerialName("Year")
    val year: String
)