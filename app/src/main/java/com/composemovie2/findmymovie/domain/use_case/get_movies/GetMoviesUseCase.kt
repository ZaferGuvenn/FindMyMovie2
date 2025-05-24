package com.composemovie2.findmymovie.domain.use_case.get_movies

import com.composemovie2.findmymovie.domain.mapper.TmdbMovieMapper 
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import com.composemovie2.findmymovie.util.Constants 
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tmdbMovieMapper: TmdbMovieMapper
) {
    fun executeGetMovies(
        genreId: String? = null,
        searchQuery: String? = null,
        fetchPopular: Boolean = false,
        fetchNowPlaying: Boolean = false, // New
        fetchUpcoming: Boolean = false,   // New
        page: Int = 1
    ): Flow<NetworkResult<List<Movie>>> {
        if (!fetchPopular && !fetchNowPlaying && !fetchUpcoming && genreId.isNullOrBlank() && searchQuery.isNullOrBlank()) {
            return flow { emit(NetworkResult.Error(message = "Operation type (popular, now playing, upcoming, genre, or search) must be specified.")) }
        }

        return flow {
            try {
                emit(NetworkResult.Loading())
                if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
                    movieRepository.getTmdbConfiguration()
                }

                val moviesResponseDto = when {
                    fetchPopular -> movieRepository.getTmdbPopularMovies(page = page)
                    fetchNowPlaying -> movieRepository.getTmdbNowPlayingMovies(page = page) // New
                    fetchUpcoming -> movieRepository.getTmdbUpcomingMovies(page = page)     // New
                    !genreId.isNullOrBlank() -> movieRepository.discoverTmdbMoviesByGenre(genreId = genreId, page = page)
                    !searchQuery.isNullOrBlank() -> movieRepository.searchTmdbMovies(query = searchQuery, page = page)
                    else -> {
                        emit(NetworkResult.Error(message = "No valid movie fetch operation specified."))
                        return@flow
                    }
                }
                
                if (moviesResponseDto.results.isNullOrEmpty()) {
                    emit(NetworkResult.Success(data = emptyList()))
                } else {
                    val movies = moviesResponseDto.results.mapNotNull { tmdbMovieMapper.map(it) }
                    emit(NetworkResult.Success(data = movies))
                }

            } catch (e: HttpException) { 
                 emit(NetworkResult.Error(message = "Server error: ${e.localizedMessage ?: "An unexpected HTTP error occurred"}"))
            } catch (e: IOException) { 
                 emit(NetworkResult.Error(message = "Network error. Check connection: ${e.localizedMessage ?: "Could not reach server"}"))
            } catch (e: Exception) { 
                 emit(NetworkResult.Error(message = "Unknown error: ${e.localizedMessage ?: "An unexpected error occurred"}"))
            }
        }
    }
}
