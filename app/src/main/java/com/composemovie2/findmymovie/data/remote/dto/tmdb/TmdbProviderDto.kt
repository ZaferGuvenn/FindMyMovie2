package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbProviderDto(
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("provider_id")
    val providerId: Int?,
    @SerializedName("provider_name")
    val providerName: String?,
    @SerializedName("display_priority")
    val displayPriority: Int?
)
