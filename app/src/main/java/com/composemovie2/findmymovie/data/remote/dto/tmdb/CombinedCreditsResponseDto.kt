package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.composemovie2.findmymovie.domain.model.CastCredit
import com.composemovie2.findmymovie.domain.model.CombinedCredits
import com.composemovie2.findmymovie.domain.model.CrewCredit
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.TVShow

data class CombinedCreditsResponseDto(
    val cast: List<CastCreditDto>,
    val crew: List<CrewCreditDto>
) {
    fun toCombinedCredits(): CombinedCredits {
        return CombinedCredits(
            cast = cast.map { it.toCastCredit() },
            crew = crew.map { it.toCrewCredit() }
        )
    }
}

data class CastCreditDto(
    val id: Int,
    val title: String?,
    val name: String?,
    val character: String?,
    val poster_path: String?,
    val media_type: String,
    val release_date: String?,
    val vote_average: Double?
) {
    fun toCastCredit(): CastCredit {
        return CastCredit(
            id = id,
            title = title,
            name = name,
            character = character,
            posterPath = poster_path,
            mediaType = media_type,
            releaseDate = release_date,
            voteAverage = vote_average
        )
    }

    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = title ?: "",
            overview = null,
            posterPath = poster_path,
            backdropPath = null,
            releaseDate = release_date,
            voteAverage = vote_average,
            voteCount = null,
            popularity = null,
            genres = null,
            status = null,
            runtime = null,
            imdbRating = null,
            imdbVotes = null,
            imdbId = null,
            budget = null,
            revenue = null,
            tagline = null,
            year = release_date?.substring(0, 4)
        )
    }

    fun toTVShow(): TVShow {
        return TVShow(
            id = id,
            name = name ?: "",
            overview = null,
            posterPath = poster_path,
            backdropPath = null,
            firstAirDate = release_date,
            voteAverage = vote_average,
            voteCount = null,
            popularity = null,
            genres = null,
            status = null,
            numberOfSeasons = null,
            numberOfEpisodes = null
        )
    }
}

data class CrewCreditDto(
    val id: Int,
    val title: String?,
    val name: String?,
    val job: String?,
    val department: String?,
    val poster_path: String?,
    val media_type: String,
    val release_date: String?,
    val vote_average: Double?
) {
    fun toCrewCredit(): CrewCredit {
        return CrewCredit(
            id = id,
            title = title,
            name = name,
            job = job,
            department = department,
            posterPath = poster_path,
            mediaType = media_type,
            releaseDate = release_date,
            voteAverage = vote_average
        )
    }
} 