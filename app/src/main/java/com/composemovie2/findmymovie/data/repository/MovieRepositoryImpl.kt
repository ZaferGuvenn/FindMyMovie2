package com.composemovie2.findmymovie.data.repository

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity
import com.composemovie2.findmymovie.data.local.MovieDao
import com.composemovie2.findmymovie.data.remote.api.MovieAPI // Ensure this is the correct API
// OMDb DTOs are likely unused in the context of the functions causing errors.
// If other functions in this file (not listed in the error report) still use them and the old MovieAPI,
// those will need separate attention, but the goal here is to fix the reported errors.
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.CombinedCreditsResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.MovieDetailResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.MovieListResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.PersonDetailResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbCountryDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProviderListResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProvidersResponseDto
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.model.PersonDetail
import com.composemovie2.findmymovie.domain.model.TVShow
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieAPI: MovieAPI, // This should be data.remote.api.MovieAPI
    private val movieDao: MovieDao
): MovieRepository {

    // These methods below use a different MovieAPI that requires an API key directly in methods.
    // They are NOT the source of the current reported errors in getMovies, getMovieDetails etc.
    // but might need review if MovieAPI DI is switched for the whole class.
    // For now, assume the injected movieAPI is data.remote.api.MovieAPI for the overridden methods below.

    @Deprecated("Use TMDB equivalent")
    override suspend fun getMoviesDto(searchString: String): MoviesDto {
        throw UnsupportedOperationException("OMDb getMoviesDto is deprecated. Use TMDB.")
    }

    @Deprecated("Use TMDB equivalent")
    override suspend fun getMovieDetailDto(imdbId: String): MovieDetailDto {
        throw UnsupportedOperationException("OMDb getMovieDetailDto is deprecated. Use TMDB.")
    }

    // The following methods are not part of the user's reported errors and seem to belong to a different API contract
    // (e.g. one where apiKey is passed directly). They will be ignored for the purpose of fixing the current errors
    // in getMovies, getMovieDetails, getPersonDetails etc. which should use the Hilt-injected movieAPI.
    // If these older methods are still needed, they might require a differently qualified MovieAPI instance.

    override suspend fun getTmdbConfiguration(): TmdbConfigurationDto {
        val response = movieAPI.getConfiguration() // Call new interface method
        if (response.isSuccessful && response.body() != null) {
            val configuration = response.body()!!
            if (!configuration.images?.secureBaseUrl.isNullOrBlank() && configuration.images?.posterSizes?.contains(Constants.DEFAULT_POSTER_SIZE) == true) {
                Constants.TMDB_IMAGE_BASE_URL = configuration.images.secureBaseUrl
            } else if (!configuration.images?.baseUrl.isNullOrBlank() && configuration.images?.posterSizes?.contains(Constants.DEFAULT_POSTER_SIZE) == true) {
                Constants.TMDB_IMAGE_BASE_URL = configuration.images.baseUrl
            }
            return configuration
        } else {
            throw java.io.IOException("Failed to fetch TMDB configuration: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun getTmdbMovieGenres(): TmdbGenresResponseDto {
        if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
            try { // Add try-catch for getTmdbConfiguration in case it fails
                getTmdbConfiguration()
            } catch (e: Exception) {
                // Log or handle error, rethrow or return empty/error state for genres
                throw java.io.IOException("Failed to get genres due to configuration error: ${e.message}", e)
            }
        }
        val response = movieAPI.getMovieGenres() // Call new interface method
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw java.io.IOException("Failed to fetch TMDB genres: ${response.code()} ${response.message()}")
        }
    }

    // The redundant methods that returned raw DTOs and used the old API key passing mechanism are now removed.
    // Their functionality is covered by the overridden methods (getMovies, getMovieDetails, searchMovies, etc.)
    // which use the new API interface and return NetworkResult<DomainModel>.

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

    // THESE METHODS BELOW ARE THE ONES CAUSING THE COMPILATION ERRORS
    // AND SHOULD USE THE HILT-INJECTED `movieAPI` which is of type `data.remote.api.MovieAPI`
    override suspend fun getMovies(page: Int): NetworkResult<List<Movie>> {
        return try {
            // movieAPI is com.composemovie2.findmymovie.data.remote.api.MovieAPI
            val response = movieAPI.getPopularMovies(page = page) // Returns Response<MovieListResponseDto>
            if (response.isSuccessful) {
                response.body()?.let { movieListResponseDto -> 
                    NetworkResult.Success(movieListResponseDto.results.map { it.toMovie() }) 
                } ?: NetworkResult.Error("Movies not found (empty body)")
            } else {
                NetworkResult.Error(response.message() ?: "Unknown error from API: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getMovieDetails(movieId: Int): NetworkResult<MovieDetail> {
        return try {
            val response = movieAPI.getMovieDetails(movieId = movieId) 
            if (response.isSuccessful) {
                response.body()?.let { movieDetailResponseDto -> 
                    NetworkResult.Success(movieDetailResponseDto.toMovieDetail()) 
                } ?: NetworkResult.Error("Movie details not found (empty body)")
            } else {
                NetworkResult.Error(response.message() ?: "Unknown error from API: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun searchMovies(query: String, page: Int): NetworkResult<List<Movie>> {
        return try {
            val response = movieAPI.searchMovies(query = query, page = page) 
            if (response.isSuccessful) {
                response.body()?.let { movieListResponseDto -> 
                    NetworkResult.Success(movieListResponseDto.results.map { it.toMovie() }) 
                } ?: NetworkResult.Error("Movies not found (empty body)")
            } else {
                NetworkResult.Error(response.message() ?: "Unknown error from API: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPersonDetails(personId: Int): NetworkResult<PersonDetail> {
        return try {
            val response = movieAPI.getPersonDetails(personId = personId) 
            if (response.isSuccessful) {
                response.body()?.let { personDetailResponseDto -> 
                    NetworkResult.Success(personDetailResponseDto.toPersonDetail()) 
                } ?: NetworkResult.Error("Person details not found (empty body)")
            } else {
                NetworkResult.Error(response.message() ?: "Unknown error from API: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPersonMovies(personId: Int): NetworkResult<List<Movie>> {
        return try {
            val response = movieAPI.getPersonCombinedCredits(personId = personId) 
            if (response.isSuccessful) {
                response.body()?.let { combinedCreditsResponseDto -> 
                    val movies = combinedCreditsResponseDto.cast 
                        .filter { it.media_type == "movie" }
                        .map { it.toMovie() } 
                    NetworkResult.Success(movies)
                } ?: NetworkResult.Error("Person movies not found (empty body)")
            } else {
                NetworkResult.Error(response.message() ?: "Unknown error from API: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPersonTVShows(personId: Int): NetworkResult<List<TVShow>> {
        return try {
            val response = movieAPI.getPersonCombinedCredits(personId = personId) 
            if (response.isSuccessful) {
                response.body()?.let { combinedCreditsResponseDto -> 
                    val tvShows = combinedCreditsResponseDto.cast 
                        .filter { it.media_type == "tv" }
                        .map { it.toTVShow() } 
                    NetworkResult.Success(tvShows)
                } ?: NetworkResult.Error("Person TV shows not found (empty body)")
            } else {
                NetworkResult.Error(response.message() ?: "Unknown error from API: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }
}
