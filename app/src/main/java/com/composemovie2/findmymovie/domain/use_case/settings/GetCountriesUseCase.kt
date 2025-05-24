package com.composemovie2.findmymovie.domain.use_case.settings

import com.composemovie2.findmymovie.domain.model.Country
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException // Added for specific exception handling
import java.io.IOException // Added for specific exception handling
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(private val repository: MovieRepository) {
    fun execute(): Flow<NetworkResult<List<Country>>> = flow {
        try {
            emit(NetworkResult.Loading())
            val countriesDto = repository.getTmdbConfigurationCountries()
            val countries = countriesDto.mapNotNull { dto ->
                if (dto.iso31661 != null && dto.englishName != null) {
                    Country(isoCode = dto.iso31661, name = dto.englishName)
                } else null
            }.sortedBy { it.name }
            emit(NetworkResult.Success(countries))
        } catch (e: HttpException) { // More specific error handling
            emit(NetworkResult.Error(message = "Server error fetching countries: ${e.localizedMessage ?: "An unexpected HTTP error occurred"}"))
        } catch (e: IOException) { // More specific error handling
            emit(NetworkResult.Error(message = "Network error fetching countries. Check connection: ${e.localizedMessage ?: "Could not reach server"}"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = e.localizedMessage ?: "Error fetching countries"))
        }
    }
}
