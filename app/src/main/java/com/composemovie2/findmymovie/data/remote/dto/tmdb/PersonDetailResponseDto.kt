package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.composemovie2.findmymovie.domain.model.PersonDetail

data class PersonDetailResponseDto(
    val id: Int,
    val name: String,
    val biography: String?,
    val profile_path: String?,
    val birthday: String?,
    val deathday: String?,
    val place_of_birth: String?,
    val known_for_department: String?,
    val popularity: Double?,
    val gender: Int?,
    val imdb_id: String?,
    val homepage: String?,
    val also_known_as: List<String>?,
    val adult: Boolean?
) {
    fun toPersonDetail(): PersonDetail {
        return PersonDetail(
            id = id,
            name = name,
            biography = biography,
            profilePath = profile_path,
            birthday = birthday,
            deathday = deathday,
            placeOfBirth = place_of_birth,
            knownForDepartment = known_for_department,
            popularity = popularity,
            gender = gender,
            imdbId = imdb_id,
            homepage = homepage,
            alsoKnownAs = also_known_as,
            adult = adult,
            combinedCredits = null // This will be populated separately
        )
    }
} 