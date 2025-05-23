package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbVideoDto(
    @SerializedName("id") // Video ID, not movie ID
    val id: String?,
    @SerializedName("iso_639_1")
    val iso6391: String?,
    @SerializedName("iso_3166_1")
    val iso31661: String?,
    @SerializedName("key")
    val key: String?, // YouTube key, etc.
    @SerializedName("name")
    val name: String?,
    @SerializedName("site")
    val site: String?, // e.g., "YouTube"
    @SerializedName("size")
    val size: Int?, // e.g., 1080
    @SerializedName("type")
    val type: String? // e.g., "Trailer", "Teaser"
)
