package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbGenreDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)
