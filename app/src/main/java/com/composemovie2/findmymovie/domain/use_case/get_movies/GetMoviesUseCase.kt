package com.composemovie2.findmymovie.domain.use_case.get_movies

// import com.composemovie2.findmymovie.domain.mapper.TmdbMovieMapper // Removed mapper import
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
// import com.composemovie2.findmymovie.util.Constants // Configuration check removed for simplification
// import kotlinx.coroutines.flow.Flow // Not returning Flow anymore
// import kotlinx.coroutines.flow.flow // Not returning Flow anymore
import okio.IOException // For specific exception handling
import retrofit2.HttpException // For specific exception handling
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
    // private val tmdbMovieMapper: TmdbMovieMapper // Removed mapper instance
) {
    // Simplified parameters and logic for now to focus on removing mapper dependency
    // Original parameters: genreId, fetchPopular, fetchNowPlaying, fetchUpcoming are ignored for now.
    suspend fun executeGetMovies(
        searchQuery: String? = null, // Keep searchQuery and page for basic functionality
        page: Int = 1
    ): NetworkResult<List<Movie>> {
        // Configuration check (Constants.TMDB_IMAGE_BASE_URL.isBlank()) is removed for simplification.
        // It should ideally be handled by the repository or a dedicated config use case/manager.

        return try {
            if (!searchQuery.isNullOrBlank()) {
                // Assuming movieRepository.searchMovies returns NetworkResult<List<Movie>>
                movieRepository.searchMovies(query = searchQuery, page = page)
            } else {
                // Default to fetching popular movies
                // Assuming movieRepository.getMovies returns NetworkResult<List<Movie>>
                movieRepository.getMovies(page = page)
            }
        } catch (e: HttpException) {
            NetworkResult.Error(message = "Server error: ${e.localizedMessage ?: "An unexpected HTTP error occurred"}")
        } catch (e: IOException) {
            NetworkResult.Error(message = "Network error. Check connection: ${e.localizedMessage ?: "Could not reach server"}")
        } catch (e: Exception) {
            NetworkResult.Error(message = "Unknown error: ${e.localizedMessage ?: "An unexpected error occurred"}")
        }
    }
}
