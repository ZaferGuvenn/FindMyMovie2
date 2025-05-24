package com.composemovie2.findmymovie.domain.use_case.settings

import com.composemovie2.findmymovie.domain.model.ProviderInfo
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException // Added for specific exception handling
import java.io.IOException // Added for specific exception handling
import javax.inject.Inject

class GetAllMovieProvidersUseCase @Inject constructor(private val repository: MovieRepository) {
    // watchRegion can be passed from user preferences later
    fun execute(watchRegion: String?): Flow<NetworkResult<List<ProviderInfo>>> = flow {
        try {
            emit(NetworkResult.Loading())
            // Ensure image base URL is loaded for provider logos
            if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
                repository.getTmdbConfiguration()
            }
            val imageBaseUrl = Constants.TMDB_IMAGE_BASE_URL
            val logoSize = "w185" // Or another suitable logo size

            val providersResponseDto = repository.getTmdbAllMovieWatchProvidersList(watchRegion)
            val providers = providersResponseDto.results?.mapNotNull { dto ->
                if (dto.providerId != null && dto.providerName != null) {
                    ProviderInfo(
                        providerId = dto.providerId,
                        providerName = dto.providerName,
                        logoPath = dto.logoPath?.let { "$imageBaseUrl$logoSize$it" } ?: ""
                    )
                } else null
            }?.sortedBy { it.providerName } ?: emptyList()
            emit(NetworkResult.Success(providers))
        } catch (e: HttpException) { // More specific error handling
            emit(NetworkResult.Error(message = "Server error fetching providers list: ${e.localizedMessage ?: "An unexpected HTTP error occurred"}"))
        } catch (e: IOException) { // More specific error handling
            emit(NetworkResult.Error(message = "Network error fetching providers list. Check connection: ${e.localizedMessage ?: "Could not reach server"}"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = e.localizedMessage ?: "Error fetching providers list"))
        }
    }
}
