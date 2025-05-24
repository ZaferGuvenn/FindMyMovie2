package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

// The "results" field is a map where keys are region codes (e.g., "US", "CA")
// and values are TmdbProviderRegionDetailsDto objects.
// Gson can handle deserializing maps.
data class TmdbWatchProvidersResponseDto(
    @SerializedName("id")
    val id: Int?, // Movie ID
    @SerializedName("results")
    val results: Map<String, TmdbProviderRegionDetailsDto>?
)
