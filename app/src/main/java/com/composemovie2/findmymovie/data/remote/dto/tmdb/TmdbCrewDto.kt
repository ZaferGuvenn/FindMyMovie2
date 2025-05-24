package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbCrewDto(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("gender")
    val gender: Int?,
    @SerializedName("id") // Crew member's TMDB ID
    val id: Int?,
    @SerializedName("known_for_department")
    val knownForDepartment: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("original_name")
    val originalName: String?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("credit_id")
    val creditId: String?,
    @SerializedName("department")
    val department: String?,
    @SerializedName("job")
    val job: String?
)

fun TmdbCrewDto.toCrewMember(): com.composemovie2.findmymovie.domain.model.CrewMember {
    return com.composemovie2.findmymovie.domain.model.CrewMember(
        id = this.id ?: 0,
        name = this.name ?: "Unknown Crew Member",
        job = this.job ?: "Unknown Job",
        department = this.department ?: "Unknown Department",
        profilePath = this.profilePath?.let { com.composemovie2.findmymovie.util.Constants.TMDB_IMAGE_BASE_URL + com.composemovie2.findmymovie.util.Constants.DEFAULT_PROFILE_SIZE + it }
    )
}
