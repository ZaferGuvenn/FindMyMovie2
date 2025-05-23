package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbImagesConfigurationDto(
    @SerializedName("base_url")
    val baseUrl: String?,
    @SerializedName("secure_base_url")
    val secureBaseUrl: String?,
    @SerializedName("poster_sizes")
    val posterSizes: List<String>?,
    @SerializedName("backdrop_sizes")
    val backdropSizes: List<String>?
    // Add other image size lists if needed, e.g., logo_sizes, profile_sizes, still_sizes
)
