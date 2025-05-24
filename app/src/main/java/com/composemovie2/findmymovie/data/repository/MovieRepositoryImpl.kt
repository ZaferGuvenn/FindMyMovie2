package com.composemovie2.findmymovie.data.repository

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity 
import com.composemovie2.findmymovie.data.local.MovieDao 
import com.composemovie2.findmymovie.data.remote.MovieAPI
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProvidersResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbCountryDto // New
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProviderListResponseDto // New
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
    private val movieAPI: MovieAPI,
    private val movieDao: MovieDao 
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

    override suspend fun getTmdbMovieWatchProviders(movieId: Int): TmdbWatchProvidersResponseDto { 
       return movieAPI.getTmdbMovieWatchProviders(movieId = movieId, apiKey = Constants.TMDB_API_KEY)
   }

    override suspend fun getTmdbConfigurationCountries(): List<TmdbCountryDto> { // New
        return movieAPI.getTmdbConfigurationCountries(apiKey = Constants.TMDB_API_KEY)
    }

    override suspend fun getTmdbAllMovieWatchProvidersList(watchRegion: String?): TmdbWatchProviderListResponseDto { // New
        return movieAPI.getTmdbAllMovieWatchProvidersList(apiKey = Constants.TMDB_API_KEY, watchRegion = watchRegion)
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

    override suspend fun getMovies(page: Int): NetworkResult<List<Movie>> {
        return try {
            val response = movieAPI.getTmdbPopularMovies(page)
            if (response.isSuccessful) {
                response.body()?.let { movieListDto ->
                    NetworkResult.Success(movieListDto.results.map { it.toMovie() })
                } ?: NetworkResult.Error("Movies not found")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getMovieDetails(movieId: Int): NetworkResult<MovieDetail> {
        return try {
            val response = movieAPI.getTmdbMovieDetails(movieId)
            if (response.isSuccessful) {
                response.body()?.let { movieDetailDto ->
                    NetworkResult.Success(movieDetailDto.toMovieDetail())
                } ?: NetworkResult.Error("Movie details not found")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun searchMovies(query: String, page: Int): NetworkResult<List<Movie>> {
        return try {
            val response = movieAPI.searchTmdbMovies(query, page)
            if (response.isSuccessful) {
                response.body()?.let { movieListDto ->
                    NetworkResult.Success(movieListDto.results.map { it.toMovie() })
                } ?: NetworkResult.Error("Movies not found")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPersonDetails(personId: Int): NetworkResult<PersonDetail> {
        return try {
            val response = movieAPI.getPersonDetails(personId)
            if (response.isSuccessful) {
                response.body()?.let { personDto ->
                    NetworkResult.Success(personDto.toPersonDetail())
                } ?: NetworkResult.Error("Person details not found")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPersonMovies(personId: Int): NetworkResult<List<Movie>> {
        return try {
            val response = movieAPI.getPersonCombinedCredits(personId)
            if (response.isSuccessful) {
                response.body()?.let { creditsDto ->
                    val movies = creditsDto.cast
                        .filter { it.media_type == "movie" }
                        .map { it.toMovie() }
                    NetworkResult.Success(movies)
                } ?: NetworkResult.Error("Person movies not found")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getPersonTVShows(personId: Int): NetworkResult<List<TVShow>> {
        return try {
            val response = movieAPI.getPersonCombinedCredits(personId)
            if (response.isSuccessful) {
                response.body()?.let { creditsDto ->
                    val tvShows = creditsDto.cast
                        .filter { it.media_type == "tv" }
                        .map { it.toTVShow() }
                    NetworkResult.Success(tvShows)
                } ?: NetworkResult.Error("Person TV shows not found")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }
}
