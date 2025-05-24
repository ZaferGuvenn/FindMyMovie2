package com.composemovie2.findmymovie.presentation.movie_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity // New import
import com.composemovie2.findmymovie.domain.model.MovieDetail // To access details for saving
import com.composemovie2.findmymovie.domain.use_case.favorites.AddFavoriteMovieUseCase // New import
import com.composemovie2.findmymovie.domain.use_case.favorites.IsMovieFavoriteUseCase // New import
import com.composemovie2.findmymovie.domain.use_case.favorites.RemoveFavoriteMovieUseCase // New import
import com.composemovie2.findmymovie.domain.use_case.get_movie_detail.GetMovieDetailUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import com.composemovie2.findmymovie.util.Constants // For poster path manipulation if needed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase,      // Injected
    private val addFavoriteMovieUseCase: AddFavoriteMovieUseCase,    // Injected
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase, // Injected
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(MovieDetailState())
    val state: State<MovieDetailState> = _state

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentMovieId: Int? = null

    init {
        savedStateHandle.get<String>("movieId")?.let { movieIdString ->
            movieIdString.toIntOrNull()?.let { id ->
                currentMovieId = id
                fetchMovieDetail(id)
                observeFavoriteStatus(id)
            } ?: run {
                _state.value = MovieDetailState(errorMsg = "Invalid Movie ID format.")
            }
        } ?: run {
            _state.value = MovieDetailState(errorMsg = "Movie ID not found.")
        }
    }

    private fun fetchMovieDetail(movieId: Int) {
        getMovieDetailUseCase.executeGetMovieDetail(movieId).onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _state.value = MovieDetailState(movieDetail = result.data, isLoading = false) // isLoading = false
                }
                is NetworkResult.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, errorMsg = null) 
                }
                is NetworkResult.Error -> {
                    _state.value = MovieDetailState(errorMsg = result.message ?: "An unknown error occurred", isLoading = false) // isLoading = false
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
                    // Extract poster path correctly from the full URL
                    val posterPathOnly = if (movieDetail.poster.startsWith(Constants.TMDB_IMAGE_BASE_URL)) {
                        movieDetail.poster.removePrefix(Constants.TMDB_IMAGE_BASE_URL)
                                       .removePrefix(Constants.DEFAULT_POSTER_SIZE) 
                    } else {
                        // Fallback for unexpected URL format, though mapper should ensure consistency
                        movieDetail.poster.substringAfterLast('/') 
                    }

                    val favoriteEntity = FavoriteMovieEntity(
                        movieId = movieId,
                        title = movieDetail.title,
                        posterPath = posterPathOnly, 
                        overview = movieDetail.overview,
                        releaseDate = movieDetail.released, 
                        voteAverage = movieDetail.imdbRating.toDoubleOrNull() 
                    )
                    addFavoriteMovieUseCase.execute(favoriteEntity)
                }
            }
        }
    }
}
