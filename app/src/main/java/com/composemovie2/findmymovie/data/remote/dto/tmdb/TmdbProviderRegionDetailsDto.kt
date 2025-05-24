package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbProviderRegionDetailsDto(
    @SerializedName("link")
    val link: String?, // Link to TMDB's "where to watch" page for this movie/region
    @SerializedName("flatrate")
    val flatrate: List<TmdbProviderDto>?, // Streaming services
    @SerializedName("rent")
    val rent: List<TmdbProviderDto>?,
    @SerializedName("buy")
    val buy: List<TmdbProviderDto>?
    // TMDB also has "ads", "free" categories for some regions/items, add if needed
)
