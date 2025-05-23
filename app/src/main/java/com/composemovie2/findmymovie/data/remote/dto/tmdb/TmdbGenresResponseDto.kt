package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbGenresResponseDto(
    @SerializedName("genres")
    val genres: List<TmdbGenreDto>?
)
