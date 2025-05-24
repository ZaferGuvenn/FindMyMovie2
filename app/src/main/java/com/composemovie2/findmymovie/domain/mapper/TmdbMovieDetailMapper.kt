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
        val baseImageUrl = Constants.TMDB_IMAGE_BASE_URL // Ensure this is populated
        val posterSize = Constants.DEFAULT_POSTER_SIZE // e.g., "w500"
        val profileSize = "w185" // A common size for profile pictures
        val backdropSize = "w780" // A common size for backdrop

        val posterUrl = from.posterPath?.let { "$baseImageUrl$posterSize$it" } ?: ""
        val backdropUrl = from.backdropPath?.let { "$baseImageUrl$backdropSize$it" }

        val genreNames = from.genresFull?.mapNotNull { it.name } ?: from.genreIds?.map { it.toString() } ?: emptyList()

        val castList = from.credits?.cast?.mapNotNull { castDto ->
            CastMember(
                id = castDto.id ?: 0,
                name = castDto.name ?: "N/A",
                character = castDto.character ?: "N/A",
                profilePath = castDto.profilePath?.let { "$baseImageUrl$profileSize$it" }
            )
        } ?: emptyList()

        // Example: Filter for Director, or take first few key crew members
        val crewList = from.credits?.crew?.mapNotNull { crewDto ->
            CrewMember(
                id = crewDto.id ?: 0,
                name = crewDto.name ?: "N/A",
                job = crewDto.job ?: "N/A",
                department = crewDto.department ?: "N/A",
                profilePath = crewDto.profilePath?.let { "$baseImageUrl$profileSize$it" }
            )
        } ?: emptyList()
        
        val director = crewList.firstOrNull { it.job == "Director" }?.name ?: ""


        val videoList = from.videos?.results?.mapNotNull { videoDto ->
            if (videoDto.site == "YouTube" && videoDto.key != null) {
                Video(
                    id = videoDto.id ?: videoDto.key!!, // Use key as fallback ID
                    key = videoDto.key!!,
                    name = videoDto.name ?: "N/A",
                    site = videoDto.site!!,
                    type = videoDto.type ?: "N/A",
                    thumbnailUrl = "https://img.youtube.com/vi/${videoDto.key}/0.jpg" // Standard YouTube thumbnail
                )
            } else {
                null
            }
        } ?: emptyList()

        return MovieDetail(
            title = from.title ?: "No title",
            year = from.releaseDate?.substringBefore("-") ?: "N/A",
            poster = posterUrl,
            released = from.releaseDate ?: "N/A",
            imdbRating = String.format("%.1f", from.voteAverage ?: 0.0),
            language = from.originalLanguage?.uppercase() ?: "N/A",
            overview = from.overview ?: "No overview available.",
            
            actors = "", // Deprecate: use castList.joinToString { it.name } if a string is needed
            awards = "", // No direct mapping from current DTO fields
            country = from.originalLanguage ?: "", // Deprecate: TMDB has production_countries list
            director = director, // Extracted from crewList
            
            backdropPath = backdropUrl,
            runtime = from.runtime,
            tagline = from.tagline?.takeIf { it.isNotBlank() }, // Use tagline only if not blank
            status = from.status,
            voteCount = from.voteCount,
            genresList = genreNames,
            
            cast = castList,
            crew = crewList, // Could be further filtered for specific roles if needed
            videos = videoList.filter { it.type == "Trailer" || it.type == "Teaser" }, // Filter for trailers/teasers
            watchProviderGroups = null
            )
    }
}
