package com.composemovie2.findmymovie.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesDto(
    @SerialName("Response")
    val response: String,
    @SerialName("Search")
    val search: List<Search>,
    @SerialName("totalResults")
    val totalResults: String
)