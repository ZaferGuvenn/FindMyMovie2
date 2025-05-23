package com.composemovie2.findmymovie.data.repository

import com.composemovie2.findmymovie.data.remote.MovieAPI
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieAPI: MovieAPI
    // No mappers injected here directly, they are used in UseCases or if repository was mapping to Domain Models
): MovieRepository {

    // OMDb - Mark as deprecated or remove
    @Deprecated("Use TMDB equivalent")
    override suspend fun getMoviesDto(searchString: String): MoviesDto {
        // return movieAPI.getOMDbMovies(searchString = searchString) // Old call
        throw UnsupportedOperationException("OMDb getMoviesDto is deprecated. Use TMDB.")
    }

    @Deprecated("Use TMDB equivalent")
    override suspend fun getMovieDetailDto(imdbId: String): MovieDetailDto {
        // return movieAPI.getOMDbMovieDetail(imdbId = imdbId) // Old call
        throw UnsupportedOperationException("OMDb getMovieDetailDto is deprecated. Use TMDB.")
    }

    // TMDB Implementations
    override suspend fun getTmdbConfiguration(): TmdbConfigurationDto {
        val configuration = movieAPI.getTmdbConfiguration(apiKey = Constants.TMDB_API_KEY)
        // Store the base URL and poster size if valid
        if (!configuration.images?.secureBaseUrl.isNullOrBlank() && configuration.images?.posterSizes?.contains(Constants.DEFAULT_POSTER_SIZE) == true) {
            Constants.TMDB_IMAGE_BASE_URL = configuration.images.secureBaseUrl
        } else if (!configuration.images?.baseUrl.isNullOrBlank() && configuration.images?.posterSizes?.contains(Constants.DEFAULT_POSTER_SIZE) == true) {
            // Fallback to non-secure URL if secure is not available but non-secure is.
            Constants.TMDB_IMAGE_BASE_URL = configuration.images.baseUrl
        }
        // Potentially store other config details like list of poster_sizes if needed elsewhere
        return configuration
    }

    override suspend fun getTmdbMovieGenres(): TmdbGenresResponseDto {
        // Ensure configuration is fetched and image base URL is set, if not already
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            getTmdbConfiguration() // Call to fetch and set the base URL
        }
        return movieAPI.getTmdbMovieGenres(apiKey = Constants.TMDB_API_KEY)
    }

    override suspend fun discoverTmdbMoviesByGenre(genreId: String, page: Int): TmdbMoviesResponseDto {
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            getTmdbConfiguration()
        }
        return movieAPI.discoverTmdbMoviesByGenre(apiKey = Constants.TMDB_API_KEY, genreId = genreId, page = page)
    }

    override suspend fun searchTmdbMovies(query: String, page: Int): TmdbMoviesResponseDto {
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            getTmdbConfiguration()
        }
        return movieAPI.searchTmdbMovies(apiKey = Constants.TMDB_API_KEY, query = query, page = page)
    }

    override suspend fun getTmdbMovieDetails(movieId: Int): TmdbMovieDto {
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            getTmdbConfiguration()
        }
        return movieAPI.getTmdbMovieDetails(movieId = movieId, apiKey = Constants.TMDB_API_KEY)
    }
}
