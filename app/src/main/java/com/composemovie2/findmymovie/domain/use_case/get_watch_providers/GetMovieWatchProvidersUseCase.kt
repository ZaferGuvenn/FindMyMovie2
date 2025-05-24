package com.composemovie2.findmymovie.domain.use_case.get_watch_providers

import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbProviderDto
import com.composemovie2.findmymovie.domain.model.WatchProvider
import com.composemovie2.findmymovie.domain.model.WatchProviderGroup
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import java.util.Locale // For device region
import javax.inject.Inject

class GetMovieWatchProvidersUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    // Defaulting to device region. UI could allow overriding this.
    fun execute(movieId: Int, region: String = Locale.getDefault().country): Flow<NetworkResult<List<WatchProviderGroup>>> = flow {
        try {
            emit(NetworkResult.Loading())

            // Ensure TMDB image base URL is loaded, as we need it for provider logos
            if (Constants.TMDB_IMAGE_BASE_URL.isBlank()) {
                // This might be called multiple times if not careful.
                // A better approach is an app-level initializer for config.
                // For now, this ensures it's loaded if somehow missed.
                repository.getTmdbConfiguration() 
            }
            val imageBaseUrl = Constants.TMDB_IMAGE_BASE_URL
            val logoSize = "w185" // Common logo size, can be from config.logo_sizes

            val responseDto = repository.getTmdbMovieWatchProviders(movieId)
            val regionalProviders = responseDto.results?.get(region.uppercase(Locale.US)) // TMDB uses uppercase region codes

            if (regionalProviders == null) {
                // No providers for the region, or region not in results.
                // Could also check other nearby regions as a fallback if desired.
                emit(NetworkResult.Success(data = emptyList())) 
                return@flow
            }

            val providerGroups = mutableListOf<WatchProviderGroup>()

            regionalProviders.flatrate?.takeIf { it.isNotEmpty() }?.let { providers ->
                providerGroups.add(
                    WatchProviderGroup(
                        type = "Stream",
                        providers = providers.mapNotNull { mapToDomain(it, imageBaseUrl, logoSize) }.sortedBy { it.displayPriority },
                        tmdbLink = regionalProviders.link
                    )
                )
            }
            regionalProviders.rent?.takeIf { it.isNotEmpty() }?.let { providers ->
                providerGroups.add(
                    WatchProviderGroup(
                        type = "Rent",
                        providers = providers.mapNotNull { mapToDomain(it, imageBaseUrl, logoSize) }.sortedBy { it.displayPriority },
                        tmdbLink = regionalProviders.link // Link is same for all types in a region
                    )
                )
            }
            regionalProviders.buy?.takeIf { it.isNotEmpty() }?.let { providers ->
                providerGroups.add(
                    WatchProviderGroup(
                        type = "Buy",
                        providers = providers.mapNotNull { mapToDomain(it, imageBaseUrl, logoSize) }.sortedBy { it.displayPriority },
                        tmdbLink = regionalProviders.link
                    )
                )
            }
            // Could add "ads", "free" if those DTO fields were included and needed

            emit(NetworkResult.Success(data = providerGroups))

        } catch (e: HttpException) {
            emit(NetworkResult.Error(message = "Server error fetching providers: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(NetworkResult.Error(message = "Network error fetching providers. Check connection: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = "Unknown error fetching providers: ${e.localizedMessage}"))
        }
    }

    private fun mapToDomain(dto: TmdbProviderDto, imageBaseUrl: String, logoSize: String): WatchProvider? {
        if (dto.providerId == null || dto.providerName == null) return null
        return WatchProvider(
            providerId = dto.providerId,
            providerName = dto.providerName,
            logoPath = dto.logoPath?.let { "$imageBaseUrl$logoSize$it" } ?: "",
            displayPriority = dto.displayPriority ?: Int.MAX_VALUE
        )
    }
}
