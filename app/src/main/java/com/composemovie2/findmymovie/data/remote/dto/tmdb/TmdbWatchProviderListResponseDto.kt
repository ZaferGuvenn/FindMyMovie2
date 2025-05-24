package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbWatchProviderListResponseDto(
    @SerializedName("results")
    val results: List<TmdbProviderDto>? // Reuses existing TmdbProviderDto
)
