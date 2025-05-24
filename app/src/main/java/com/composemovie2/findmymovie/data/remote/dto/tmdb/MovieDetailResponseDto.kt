package com.composemovie2.findmymovie.data.remote.dto.tmdb

data class MovieDetailResponseDto(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val popularity: Double?,
    val genres: List<GenreDto>?,
    val status: String?,
    val runtime: Int?,
    val imdb_id: String?,
    val budget: Long?,
    val revenue: Long?,
    val tagline: String?,
    val adult: Boolean?,
    val original_language: String?,
    val original_title: String?,
    val video: Boolean?
) {
    fun toMovieDetail(): com.composemovie2.findmymovie.domain.model.MovieDetail {
        return com.composemovie2.findmymovie.domain.model.MovieDetail(
            id = id,
            title = title,
            overview = overview,
            posterPath = poster_path,
            backdropPath = backdrop_path,
            releaseDate = release_date,
            voteAverage = vote_average,
            voteCount = vote_count,
            popularity = popularity,
            genres = genres?.map { it.toGenre() },
            status = status,
            runtime = runtime,
            imdbId = imdb_id,
            budget = budget,
            revenue = revenue,
            tagline = tagline,
            year = release_date?.substring(0, 4)
        )
    }
}

data class GenreDto(
    val id: Int,
    val name: String
) {
    fun toGenre(): com.composemovie2.findmymovie.domain.model.Genre {
        return com.composemovie2.findmymovie.domain.model.Genre(
            id = id,
            name = name
        )
    }
} 