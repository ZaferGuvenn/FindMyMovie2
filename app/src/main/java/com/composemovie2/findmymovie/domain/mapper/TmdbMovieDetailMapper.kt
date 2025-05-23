package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.util.Constants
import javax.inject.Inject

class TmdbMovieDetailMapper @Inject constructor() : Mapper<TmdbMovieDto, MovieDetail> {
    override fun map(from: TmdbMovieDto): MovieDetail {
        val posterUrl = if (from.posterPath.isNullOrBlank()) {
            "" // Or a placeholder image URL
        } else {
            // Ensure Constants.TMDB_IMAGE_BASE_URL is populated before this is called
            // Typically fetched during app startup or first API call by repository
            "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_POSTER_SIZE}${from.posterPath}"
        }
        return MovieDetail(
            actors = "", // Requires specific API call or append_to_response=credits
            awards = "", // Requires specific API call
            country = from.originalLanguage ?: "", // originalLanguage is available, not full country list
            director = "", // Requires specific API call or append_to_response=credits
            imdbRating = String.format("%.1f", from.voteAverage ?: 0.0), // Format to one decimal place
            language = from.originalLanguage?.uppercase() ?: "N/A",
            poster = posterUrl,
            released = from.releaseDate ?: "N/A",
            title = from.title ?: "No title",
            year = from.releaseDate?.substringBefore("-") ?: "N/A",
            overview = from.overview ?: "No overview available.", // Added
            genres = from.genreIds?.joinToString(", ") ?: "N/A" // Added: genre IDs as string
        )
    }
}
