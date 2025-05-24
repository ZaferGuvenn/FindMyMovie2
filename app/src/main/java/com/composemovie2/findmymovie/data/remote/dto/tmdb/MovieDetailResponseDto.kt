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
    val video: Boolean?,
    val credits: TmdbCreditsDto?, // Added credits
    val videos: TmdbVideosResponseDto? // Added videos
) {
    fun toMovieDetail(): com.composemovie2.findmymovie.domain.model.MovieDetail {
        val baseImageUrl = com.composemovie2.findmymovie.util.Constants.TMDB_IMAGE_BASE_URL
        val posterSize = com.composemovie2.findmymovie.util.Constants.DEFAULT_POSTER_SIZE
        val backdropSize = com.composemovie2.findmymovie.util.Constants.DEFAULT_BACKDROP_SIZE

        return com.composemovie2.findmymovie.domain.model.MovieDetail(
            id = id,
            title = title,
            overview = overview,
            posterPath = poster_path?.let { baseImageUrl + posterSize + it },
            backdropPath = backdrop_path?.let { baseImageUrl + backdropSize + it },
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
            year = release_date?.substringBefore("-") ?: "",
            videos = this.videos?.results?.mapNotNull { it.toVideo() }?.filter { it.site == "YouTube" && (it.type == "Trailer" || it.type == "Teaser") } ?: emptyList(),
            cast = this.credits?.cast?.mapNotNull { it.toCastMember() } ?: emptyList(),
            crew = this.credits?.crew?.mapNotNull { it.toCrewMember() } ?: emptyList()
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