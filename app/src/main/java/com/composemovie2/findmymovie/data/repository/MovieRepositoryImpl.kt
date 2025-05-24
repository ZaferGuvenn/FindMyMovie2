package com.composemovie2.findmymovie.data.repository

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity // New import
import com.composemovie2.findmymovie.data.local.MovieDao // New import
import com.composemovie2.findmymovie.data.remote.MovieAPI
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
import kotlinx.coroutines.flow.Flow // New import
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieAPI: MovieAPI,
    private val movieDao: MovieDao // Added MovieDao to constructor
): MovieRepository {

    @Deprecated("Use TMDB equivalent")
    override suspend fun getMoviesDto(searchString: String): MoviesDto {
        throw UnsupportedOperationException("OMDb getMoviesDto is deprecated. Use TMDB.")
    }

    @Deprecated("Use TMDB equivalent")
    override suspend fun getMovieDetailDto(imdbId: String): MovieDetailDto {
        throw UnsupportedOperationException("OMDb getMovieDetailDto is deprecated. Use TMDB.")
    }

    override suspend fun getTmdbConfiguration(): TmdbConfigurationDto {
        val configuration = movieAPI.getTmdbConfiguration(apiKey = Constants.TMDB_API_KEY)
        if (!configuration.images?.secureBaseUrl.isNullOrBlank() && configuration.images?.posterSizes?.contains(Constants.DEFAULT_POSTER_SIZE) == true) {
            Constants.TMDB_IMAGE_BASE_URL = configuration.images.secureBaseUrl
        } else if (!configuration.images?.baseUrl.isNullOrBlank() && configuration.images?.posterSizes?.contains(Constants.DEFAULT_POSTER_SIZE) == true) {
            Constants.TMDB_IMAGE_BASE_URL = configuration.images.baseUrl
        }
        return configuration
    }

    override suspend fun getTmdbMovieGenres(): TmdbGenresResponseDto {
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            getTmdbConfiguration() 
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

    override suspend fun getTmdbPopularMovies(page: Int): TmdbMoviesResponseDto {
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            getTmdbConfiguration() 
        }
        return movieAPI.getTmdbPopularMovies(apiKey = Constants.TMDB_API_KEY, page = page)
    }

    override suspend fun getTmdbNowPlayingMovies(page: Int): TmdbMoviesResponseDto { 
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) { getTmdbConfiguration() }
        return movieAPI.getTmdbNowPlayingMovies(apiKey = Constants.TMDB_API_KEY, page = page)
    }

    override suspend fun getTmdbUpcomingMovies(page: Int): TmdbMoviesResponseDto { 
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) { getTmdbConfiguration() }
        return movieAPI.getTmdbUpcomingMovies(apiKey = Constants.TMDB_API_KEY, page = page)
    }

    // Favorite Movie Implementations
    override fun getFavoriteMovies(): Flow<List<FavoriteMovieEntity>> {
        return movieDao.getFavoriteMovies()
    }

    override fun isFavorite(movieId: Int): Flow<Boolean> {
        return movieDao.isFavorite(movieId)
    }

    override suspend fun addFavorite(movie: FavoriteMovieEntity) {
        movieDao.addFavorite(movie)
    }

    override suspend fun removeFavoriteById(movieId: Int) {
        movieDao.removeFavoriteById(movieId)
    }
       
    override suspend fun getFavoriteMovieById(movieId: Int): FavoriteMovieEntity? {
        return movieDao.getFavoriteMovieById(movieId)
    }
}
