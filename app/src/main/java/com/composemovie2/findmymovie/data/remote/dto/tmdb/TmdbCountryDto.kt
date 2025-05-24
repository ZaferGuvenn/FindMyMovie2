package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbCountryDto(
    @SerializedName("iso_3166_1")
    val iso31661: String?, // e.g., "US"
    @SerializedName("english_name")
    val englishName: String?,
    @SerializedName("native_name") // Optional, might not always be present or needed
    val nativeName: String?
)
