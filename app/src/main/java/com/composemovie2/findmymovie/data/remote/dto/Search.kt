package com.composemovie2.findmymovie.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Search(
    @SerialName("imdbID")
    val imdbID: String,
    @SerialName("Poster")
    val poster: String,
    @SerialName("Title")
    val title: String,
    @SerialName("Type")
    val type: String,
    @SerialName("Year")
    val year: String
)