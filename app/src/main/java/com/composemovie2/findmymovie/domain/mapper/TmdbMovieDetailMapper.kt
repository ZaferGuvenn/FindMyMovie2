package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.domain.model.CastMember
import com.composemovie2.findmymovie.domain.model.CrewMember
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.model.Video
import com.composemovie2.findmymovie.util.Constants
import javax.inject.Inject

class TmdbMovieDetailMapper @Inject constructor() : Mapper<TmdbMovieDto, MovieDetail> {
    override fun map(from: TmdbMovieDto): MovieDetail {
        val posterUrl = if (from.posterPath.isNullOrBlank()) {
            "" // Or a placeholder image URL
        } else {
            "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_POSTER_SIZE}${from.posterPath}"
        }

        val backdropUrl = if (from.backdropPath.isNullOrBlank()) {
            null
        } else {
            "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_BACKDROP_SIZE}${from.backdropPath}"
        }

        // Get genre names from either genresFull (movie details) or genreIds (movie list)
        val genresList = if (!from.genresFull.isNullOrEmpty()) {
            from.genresFull.mapNotNull { it.name }
        } else if (!from.genreIds.isNullOrEmpty()) {
            // If we only have genre IDs, we'll need to map them to names
            // This would ideally come from a cached genre mapping
            from.genreIds.map { "Genre $it" }
        } else {
            emptyList()
        }

        // Get cast and crew from credits if available
        val cast = from.credits?.cast?.map { castMember ->
            CastMember(
                id = castMember.id ?: 0,
                name = castMember.name ?: "",
                character = castMember.character ?: "",
                profilePath = castMember.profilePath?.let { 
                    "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_PROFILE_SIZE}$it"
                }
            )
        } ?: emptyList()

        val crew = from.credits?.crew?.map { crewMember ->
            CrewMember(
                id = crewMember.id ?: 0,
                name = crewMember.name ?: "",
                job = crewMember.job ?: "",
                department = crewMember.department ?: "",
                profilePath = crewMember.profilePath?.let {
                    "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_PROFILE_SIZE}$it"
                }
            )
        } ?: emptyList()

        // Get videos if available
        val videos = from.videos?.results?.map { video ->
            Video(
                id = video.id ?: "",
                name = video.name ?: "",
                key = video.key ?: "",
                site = video.site ?: "",
                type = video.type ?: "",
                thumbnailUrl = ""
            )
        } ?: emptyList()

        return MovieDetail(
            title = from.title ?: "No title",
            year = from.releaseDate?.substringBefore("-") ?: "N/A",
            poster = posterUrl,
            released = from.releaseDate ?: "N/A",
            imdbRating = String.format("%.1f", from.voteAverage ?: 0.0),
            language = from.originalLanguage?.uppercase() ?: "N/A",
            overview = from.overview ?: "No overview available.",
            actors = cast.take(3).joinToString(", ") { it.name }, // First 3 actors
            awards = "", // TMDB doesn't provide awards
            country = from.originalLanguage ?: "", // Using original language as country for now
            director = crew.find { it.job.equals("Director", ignoreCase = true) }?.name ?: "",
            backdropPath = backdropUrl,
            runtime = from.runtime,
            tagline = from.tagline,
            status = from.status,
            voteCount = from.voteCount,
            genresList = genresList,
            cast = cast,
            crew = crew,
            videos = videos
        )
    }
}
