package com.composemovie2.findmymovie.domain.repository

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb - to be removed
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb - to be removed
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto // For TMDB movie detail
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto

interface MovieRepository {
    // OMDb methods (to be removed or fully replaced)
    suspend fun getMoviesDto(searchString: String): MoviesDto // This will be replaced
    suspend fun getMovieDetailDto(imdbId: String): MovieDetailDto // This will be replaced

    // TMDB methods
    suspend fun getTmdbConfiguration(): TmdbConfigurationDto
    suspend fun getTmdbMovieGenres(): TmdbGenresResponseDto // Will be mapped to List<Genre> in UseCase
    suspend fun discoverTmdbMoviesByGenre(genreId: String, page: Int): TmdbMoviesResponseDto // Mapped in UseCase
    suspend fun searchTmdbMovies(query: String, page: Int): TmdbMoviesResponseDto // Mapped in UseCase
    suspend fun getTmdbMovieDetails(movieId: Int): TmdbMovieDto // Mapped in UseCase
}
