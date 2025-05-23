package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.util.Constants
import javax.inject.Inject

class TmdbMovieMapper @Inject constructor() : Mapper<TmdbMovieDto, Movie> {
    override fun map(from: TmdbMovieDto): Movie {
        val posterUrl = if (from.posterPath.isNullOrBlank()) {
            "" // Or a placeholder image URL
        } else {
            "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_POSTER_SIZE}${from.posterPath}"
        }
        return Movie(
            imdbId = from.id?.toString() ?: "", // Using TMDB's int ID as imdbId for now. This might need adjustment if true IMDb ID is needed.
                                             // TMDB provides its own 'id', not 'imdb_id' directly in discover results.
                                             // We can fetch imdb_id via the /movie/{movie_id}/external_ids endpoint if strictly needed.
            title = from.title ?: "No title",
            year = from.releaseDate?.substringBefore("-") ?: "N/A", // Extract year from YYYY-MM-DD
            poster = posterUrl,
            type = "movie" // TMDB discover/movie endpoint returns movies. We can add 'type' if TMDB provides it.
                           // The 'TmdbMovieDto' doesn't have a 'type' field like OMDb's 'Search' DTO.
                           // For now, defaulting to "movie". This might need refinement if we mix TMDB types.
        )
    }
}
