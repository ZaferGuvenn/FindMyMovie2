package com.composemovie2.findmymovie.data.remote.dto.tmdb

data class MovieListResponseDto(
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int,
    val total_results: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val popularity: Double?,
    val genre_ids: List<Int>?,
    val adult: Boolean?,
    val original_language: String?,
    val original_title: String?,
    val video: Boolean?
) {
    fun toMovie(): com.composemovie2.findmymovie.domain.model.Movie {
        return com.composemovie2.findmymovie.domain.model.Movie(
            id = id,
            title = title,
            overview = overview,
            posterPath = poster_path,
            backdropPath = backdrop_path,
            releaseDate = release_date,
            voteAverage = vote_average,
            voteCount = vote_count,
            popularity = popularity,
            genres = null, // This will be populated separately
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
} 