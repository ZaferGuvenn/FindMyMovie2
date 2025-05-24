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

fun TmdbVideoDto.toVideo(): com.composemovie2.findmymovie.domain.model.Video {
    return com.composemovie2.findmymovie.domain.model.Video(
        id = this.id ?: this.key ?: java.util.UUID.randomUUID().toString(), // Ensure ID is not empty
        key = this.key ?: "",
        name = this.name ?: "Unknown Video",
        site = this.site ?: "Unknown Site",
        type = this.type ?: "Unknown Type",
        thumbnailUrl = if (this.site == "YouTube" && !this.key.isNullOrBlank()) "https://img.youtube.com/vi/${this.key}/0.jpg" else ""
    )
}
