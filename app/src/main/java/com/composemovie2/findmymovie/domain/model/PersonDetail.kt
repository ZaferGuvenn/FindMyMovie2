package com.composemovie2.findmymovie.domain.model

data class PersonDetail(
    val id: Int,
    val name: String,
    val biography: String?,
    val profilePath: String?,
    val birthday: String?,
    val deathday: String?,
    val placeOfBirth: String?,
    val knownForDepartment: String?,
    val popularity: Double?,
    val gender: Int?,
    val imdbId: String?,
    val homepage: String?,
    val alsoKnownAs: List<String>?,
    val adult: Boolean?,
    val combinedCredits: CombinedCredits?
)

data class CombinedCredits(
    val cast: List<CastCredit> = emptyList(),
    val crew: List<CrewCredit> = emptyList()
)

data class CastCredit(
    val id: Int,
    val title: String?,
    val name: String?,
    val character: String?,
    val posterPath: String?,
    val mediaType: String,
    val releaseDate: String?,
    val voteAverage: Double?
)

data class CrewCredit(
    val id: Int,
    val title: String?,
    val name: String?,
    val job: String?,
    val department: String?,
    val posterPath: String?,
    val mediaType: String,
    val releaseDate: String?,
    val voteAverage: Double?
) 