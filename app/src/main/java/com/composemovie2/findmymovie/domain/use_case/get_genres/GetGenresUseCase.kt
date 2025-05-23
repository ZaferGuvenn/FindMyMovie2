package com.composemovie2.findmymovie.domain.use_case.get_genres

import com.composemovie2.findmymovie.domain.mapper.TmdbGenreMapper
import com.composemovie2.findmymovie.domain.model.Genre
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tmdbGenreMapper: TmdbGenreMapper
) {
    fun executeGetGenres(): Flow<NetworkResult<List<Genre>>> = flow {
        try {
            emit(NetworkResult.Loading())
            // First, ensure TMDB configuration is loaded, especially if not done elsewhere
            // This helps ensure TMDB_IMAGE_BASE_URL is available if any genre images were hypothetically used.
            // Though for genres, it's less critical than for movies.
            if (com.composemovie2.findmymovie.util.Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
                movieRepository.getTmdbConfiguration()
            }

            val genresResponseDto = movieRepository.getTmdbMovieGenres()
            val genres = genresResponseDto.genres?.mapNotNull { tmdbGenreMapper.map(it) } ?: emptyList()

            if (genres.isNotEmpty()) {
                emit(NetworkResult.Success(data = genres))
            } else {
                // This case might occur if genresResponseDto.genres is null or empty
                // Or if all genres failed to map (e.g. all had null IDs/names which mapper might filter)
                emit(NetworkResult.Error(message = "No genres found or failed to map genres."))
            }

        } catch (e: HttpException) {
            emit(NetworkResult.Error(message = "An error occurred while connecting to the server: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(NetworkResult.Error(message = "Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = "An unknown error occurred: ${e.localizedMessage}"))
        }
    }
}
