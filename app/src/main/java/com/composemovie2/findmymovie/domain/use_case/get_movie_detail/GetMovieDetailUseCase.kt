package com.composemovie2.findmymovie.domain.use_case.get_movie_detail

import com.composemovie2.findmymovie.domain.mapper.TmdbMovieDetailMapper // Changed mapper
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tmdbMovieDetailMapper: TmdbMovieDetailMapper // Changed mapper instance
) {

    // Parameter changed to movieId: Int for TMDB
    fun executeGetMovieDetail(movieId: Int): Flow<NetworkResult<MovieDetail>> { 
        return flow {
            try {
                emit(NetworkResult.Loading())

                // Ensure TMDB configuration is loaded (especially image base URL)
                if (com.composemovie2.findmymovie.util.Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
                    movieRepository.getTmdbConfiguration()
                }

                val movieDetailDto = movieRepository.getTmdbMovieDetails(movieId) // Call new repository method
                // movieDetailDto here is actually TmdbMovieDto, which the new mapper handles
                
                emit(NetworkResult.Success(tmdbMovieDetailMapper.map(movieDetailDto)))

            } catch (e: HttpException) {
                emit(NetworkResult.Error(message = "An error occurred while connecting to the server: ${e.localizedMessage}"))
            } catch (e: IOException) {
                emit(NetworkResult.Error(message = "Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                emit(NetworkResult.Error(message = "An unknown error occurred: ${e.localizedMessage}"))
            }
        }
    }
}
