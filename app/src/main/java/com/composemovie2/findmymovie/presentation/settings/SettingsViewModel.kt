package com.composemovie2.findmymovie.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.data.preferences.UserPreferencesRepository
import com.composemovie2.findmymovie.data.preferences.UserWatchPreferences
import com.composemovie2.findmymovie.domain.model.Country
import com.composemovie2.findmymovie.domain.model.ProviderInfo
import com.composemovie2.findmymovie.domain.use_case.settings.GetAllMovieProvidersUseCase
import com.composemovie2.findmymovie.domain.use_case.settings.GetCountriesUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SettingsScreenState(
    val countries: List<Country> = emptyList(),
    val isLoadingCountries: Boolean = false,
    val countriesError: String? = null,

    val providers: List<ProviderInfo> = emptyList(),
    val isLoadingProviders: Boolean = false,
    val providersError: String? = null,

    val currentPreferences: UserWatchPreferences = UserWatchPreferences(Locale.getDefault().country.uppercase(Locale.US), emptySet())
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getAllMovieProvidersUseCase: GetAllMovieProvidersUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenState())
    val uiState: StateFlow<SettingsScreenState> = _uiState.asStateFlow()

    init {
        loadInitialPreferences()
        fetchCountries()
        // Fetch providers once initial region preference is loaded
        viewModelScope.launch {
            userPreferencesRepository.userWatchPreferencesFlow.collectLatest { prefs ->
                _uiState.update { it.copy(currentPreferences = prefs) }
                fetchProvidersForRegion(prefs.selectedRegion)
            }
        }
    }

    private fun loadInitialPreferences() {
         viewModelScope.launch {
             userPreferencesRepository.userWatchPreferencesFlow.firstOrNull()?.let { prefs ->
                 _uiState.update { it.copy(currentPreferences = prefs) }
             }
         }
    }

    private fun fetchCountries() {
        getCountriesUseCase.execute()
            .onEach { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoadingCountries = true) }
                    is NetworkResult.Success -> _uiState.update { it.copy(countries = result.data ?: emptyList(), isLoadingCountries = false) }
                    is NetworkResult.Error -> _uiState.update { it.copy(countriesError = result.message, isLoadingCountries = false) }
                }
            }.launchIn(viewModelScope)
    }

    private fun fetchProvidersForRegion(region: String) {
        getAllMovieProvidersUseCase.execute(watchRegion = region)
            .onEach { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoadingProviders = true) }
                    is NetworkResult.Success -> _uiState.update { it.copy(providers = result.data ?: emptyList(), isLoadingProviders = false) }
                    is NetworkResult.Error -> _uiState.update { it.copy(providersError = result.message, isLoadingProviders = false) }
                }
            }.launchIn(viewModelScope)
    }

    fun updateSelectedRegion(regionCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedRegion(regionCode)
            // Flow collection in init will trigger provider refetch
        }
    }

    fun toggleSubscriptionProvider(providerId: String) {
        viewModelScope.launch {
            val currentPrefs = _uiState.value.currentPreferences
            val currentSubscribed = currentPrefs.subscribedProviderIds.toMutableSet()
            if (currentSubscribed.contains(providerId)) {
                currentSubscribed.remove(providerId)
            } else {
                currentSubscribed.add(providerId)
            }
            userPreferencesRepository.updateSubscribedProviders(currentSubscribed)
        }
    }
}
