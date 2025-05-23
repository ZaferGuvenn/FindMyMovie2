package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbConfigurationDto(
    @SerializedName("images")
    val images: TmdbImagesConfigurationDto?
    // Can include other configuration fields if necessary, like "change_keys"
)
