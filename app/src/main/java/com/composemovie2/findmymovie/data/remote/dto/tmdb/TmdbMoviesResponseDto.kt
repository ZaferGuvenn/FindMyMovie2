package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbMoviesResponseDto(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("results")
    val results: List<TmdbMovieDto>?,
    @SerializedName("total_pages")
    val totalPages: Int?,
    @SerializedName("total_results")
    val totalResults: Int?
)
