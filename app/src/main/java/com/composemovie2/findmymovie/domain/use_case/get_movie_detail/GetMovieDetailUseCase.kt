package com.composemovie2.findmymovie.domain.use_case.get_movie_detail

// import com.composemovie2.findmymovie.domain.mapper.TmdbMovieDetailMapper // Removed mapper import
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
// import kotlinx.coroutines.flow.Flow // Not returning Flow anymore
// import kotlinx.coroutines.flow.flow // Not returning Flow anymore
import okio.IOException // For specific exception handling
import retrofit2.HttpException // For specific exception handling
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository
    // private val tmdbMovieDetailMapper: TmdbMovieDetailMapper // Removed mapper instance
) {

    // Changed to suspend fun returning NetworkResult<MovieDetail> directly
    suspend fun executeGetMovieDetail(movieId: Int): NetworkResult<MovieDetail> { 
        // The configuration check should ideally be handled by the repository or a dedicated config use case/manager
        // if (com.composemovie2.findmymovie.util.Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
        //     try {
        //         movieRepository.getTmdbConfiguration() // This also needs to be a suspend call if repository changes
        //     } catch (e: Exception) {
        //          // Handle config loading failure, perhaps return error or log
        //          // For now, assume it's handled elsewhere or not critical path for this specific use case call
        //     }
        // }
        
        return try {
            // Assuming movieRepository.getMovieDetails(movieId) is a suspend function
            // that returns NetworkResult<MovieDetail>
            movieRepository.getMovieDetails(movieId)
        } catch (e: HttpException) {
            NetworkResult.Error(message = "An error occurred while connecting to the server: ${e.localizedMessage ?: "Unknown HTTP error"}")
        } catch (e: IOException) {
            NetworkResult.Error(message = "Couldn't reach server. Check your internet connection: ${e.localizedMessage ?: "Unknown IO error"}")
        } catch (e: Exception) { // Catching a broader range of exceptions
            NetworkResult.Error(message = "An unknown error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
