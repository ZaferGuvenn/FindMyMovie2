package com.composemovie2.findmymovie.domain.use_case.get_movies

import com.composemovie2.findmymovie.domain.mapper.TmdbMovieMapper // Changed mapper
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tmdbMovieMapper: TmdbMovieMapper // Changed mapper instance
) {

    // Parameters updated for TMDB: either genreId or searchQuery, plus page
    fun executeGetMovies(
        genreId: String? = null,
        searchQuery: String? = null,
        page: Int = 1
    ): Flow<NetworkResult<List<Movie>>> {
        // Basic validation: At least one of genreId or searchQuery should be provided,
        // but not necessarily enforced here as ViewModel might decide the logic.
        // For now, we assume ViewModel calls this appropriately.
        if (genreId.isNullOrBlank() && searchQuery.isNullOrBlank()) {
            return flow { emit(NetworkResult.Error(message = "Either a genre ID or a search query must be provided.")) }
        }

        return flow {
            try {
                emit(NetworkResult.Loading())

                // Ensure TMDB configuration is loaded (especially image base URL)
                if (com.composemovie2.findmymovie.util.Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
                    movieRepository.getTmdbConfiguration()
                }

                val moviesResponseDto = if (!genreId.isNullOrBlank()) {
                    movieRepository.discoverTmdbMoviesByGenre(genreId = genreId, page = page)
                } else if (!searchQuery.isNullOrBlank()) {
                    movieRepository.searchTmdbMovies(query = searchQuery, page = page)
                } else {
                    // This case should ideally be caught by the initial check,
                    // but as a fallback:
                    emit(NetworkResult.Error(message = "No valid operation: genre ID or search query required."))
                    return@flow // End the flow here
                }
                
                // The DTO structure for both discover and search in TMDB is TmdbMoviesResponseDto
                // It contains a "results" list of TmdbMovieDto

                if (moviesResponseDto.results.isNullOrEmpty()) {
                    if (moviesResponseDto.totalPages == 0 || moviesResponseDto.totalResults == 0) {
                         emit(NetworkResult.Error(message = "No movies found matching your criteria."))
                    } else {
                        // This could be a valid empty page if page > totalPages, or an API issue
                        emit(NetworkResult.Success(data = emptyList()))
                    }
                } else {
                    val movies = moviesResponseDto.results.mapNotNull { tmdbMovieMapper.map(it) }
                    emit(NetworkResult.Success(data = movies))
                }

            } catch (e: HttpException) {
                emit(NetworkResult.Error(message = "Server error: ${e.localizedMessage}"))
            } catch (e: IOException) {
                emit(NetworkResult.Error(message = "Network error. Check connection: ${e.localizedMessage}"))
            } catch (e: Exception) {
                emit(NetworkResult.Error(message = "Unknown error: ${e.localizedMessage}"))
            }
        }
    }
}
