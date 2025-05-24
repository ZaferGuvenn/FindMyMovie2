package com.composemovie2.findmymovie.presentation.movie_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity 
import com.composemovie2.findmymovie.domain.model.MovieDetail 
import com.composemovie2.findmymovie.domain.use_case.favorites.AddFavoriteMovieUseCase 
import com.composemovie2.findmymovie.domain.use_case.favorites.IsMovieFavoriteUseCase 
import com.composemovie2.findmymovie.domain.use_case.favorites.RemoveFavoriteMovieUseCase 
import com.composemovie2.findmymovie.domain.use_case.get_movie_detail.GetMovieDetailUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import com.composemovie2.findmymovie.util.Constants 
import com.composemovie2.findmymovie.domain.model.WatchProviderGroup 
import com.composemovie2.findmymovie.domain.use_case.get_watch_providers.GetMovieWatchProvidersUseCase 
import com.composemovie2.findmymovie.data.preferences.UserPreferencesRepository // New
import com.composemovie2.findmymovie.data.preferences.UserWatchPreferences // New
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale 
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase,
    private val addFavoriteMovieUseCase: AddFavoriteMovieUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase,
    private val getMovieWatchProvidersUseCase: GetMovieWatchProvidersUseCase,
    private val userPreferencesRepository: UserPreferencesRepository, // Injected
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(MovieDetailState())
    val state: State<MovieDetailState> = _state

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentMovieId: Int? = null
    private var currentUserPreferences: UserWatchPreferences? = null // To store latest prefs

    init {
        viewModelScope.launch {
             // Collect preferences first
             userPreferencesRepository.userWatchPreferencesFlow
                 .distinctUntilChanged()
                 .collectLatest { prefs ->
                     currentUserPreferences = prefs
                     _state.value = _state.value.copy(subscribedProviderIds = prefs.subscribedProviderIds)
                     // If movieId is already known, re-fetch providers for the new region
                     currentMovieId?.let { movieId ->
                         fetchWatchProviders(movieId, prefs.selectedRegion)
                     }
                 }
        }

        savedStateHandle.get<String>("movieId")?.let { movieIdString ->
            movieIdString.toIntOrNull()?.let { id ->
                currentMovieId = id
                fetchMovieDetail(id)
                observeFavoriteStatus(id)
                // Initial fetch of providers will use region from collected prefs or default if prefs not yet emitted
                currentUserPreferences?.let { prefs ->
                    fetchWatchProviders(id, prefs.selectedRegion)
                } ?: fetchWatchProviders(id) // Use default region if prefs not yet loaded
            } ?: run {
                _state.value = _state.value.copy(errorMsg = "Invalid Movie ID format.")
            }
        } ?: run {
            _state.value = _state.value.copy(errorMsg = "Movie ID not found.")
        }
    }

    private fun fetchMovieDetail(movieId: Int) {
        getMovieDetailUseCase.executeGetMovieDetail(movieId).onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(movieDetail = result.data, isLoading = false, errorMsg = null)
                }
                is NetworkResult.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, errorMsg = null)
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, errorMsg = result.message ?: "An unknown error occurred fetching details")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeFavoriteStatus(movieId: Int) {
        isMovieFavoriteUseCase.execute(movieId)
            .onEach { isFav -> _isFavorite.value = isFav }
            .launchIn(viewModelScope)
     }

    fun toggleFavoriteStatus() {
        viewModelScope.launch {
            val movieDetail = _state.value.movieDetail
            val movieId = currentMovieId
            if (movieDetail != null && movieId != null) {
                if (_isFavorite.value) {
                    removeFavoriteMovieUseCase.execute(movieId)
                } else {
                    val posterPathOnly = if (movieDetail.poster.startsWith(Constants.TMDB_IMAGE_BASE_URL)) {
                        movieDetail.poster.removePrefix(Constants.TMDB_IMAGE_BASE_URL)
                                       .removePrefix(Constants.DEFAULT_POSTER_SIZE) 
                    } else {
                        movieDetail.poster.substringAfterLast('/') 
                    }
                    val favoriteEntity = FavoriteMovieEntity(
                        movieId = movieId, title = movieDetail.title, posterPath = posterPathOnly, 
                        overview = movieDetail.overview, releaseDate = movieDetail.released, 
                        voteAverage = movieDetail.imdbRating.toDoubleOrNull() 
                    )
                    addFavoriteMovieUseCase.execute(favoriteEntity)
                }
            }
        }
     }
    
    private fun fetchWatchProviders(movieId: Int, region: String? = null) {
        val regionToUse = region ?: currentUserPreferences?.selectedRegion ?: Locale.getDefault().country.uppercase(Locale.US)
        getMovieWatchProvidersUseCase.execute(movieId, regionToUse).onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        watchProviderGroups = result.data,
                        isLoadingWatchProviders = false,
                        watchProvidersError = null
                        // subscribedProviderIds is already updated by the flow collection in init
                    )
                }
                is NetworkResult.Loading -> {
                    _state.value = _state.value.copy(isLoadingWatchProviders = true, watchProvidersError = null)
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingWatchProviders = false,
                        watchProvidersError = result.message ?: "Error fetching watch providers"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
       
    fun retryFetchMovieDetail() { 
         currentMovieId?.let { movieId ->
             fetchMovieDetail(movieId)
             fetchWatchProviders(movieId, currentUserPreferences?.selectedRegion ?: Locale.getDefault().country.uppercase(Locale.US))
         }
    }
}
