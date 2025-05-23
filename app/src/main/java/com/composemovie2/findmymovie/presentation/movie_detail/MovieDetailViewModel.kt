package com.composemovie2.findmymovie.presentation.movie_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.use_case.get_movie_detail.GetMovieDetailUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(MovieDetailState())
    val state: State<MovieDetailState> = _state

    init {
        savedStateHandle.get<String>("movieId")?.let { movieIdString ->
            // MovieId from navigation is a String, but TMDB uses Int.
            // It was stored as movie.imdbId (which is TMDB int ID as String) in MovieListRow.
            val movieIdInt = movieIdString.toIntOrNull()
            if (movieIdInt != null) {
                fetchMovieDetail(movieIdInt)
            } else {
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
                    _state.value = MovieDetailState(movieDetail = result.data)
                }
                is NetworkResult.Loading -> {
                    _state.value = MovieDetailState(isLoading = true)
                }
                is NetworkResult.Error -> {
                    _state.value = MovieDetailState(errorMsg = result.message ?: "An unknown error occurred")
                }
            }
        }.launchIn(viewModelScope)
    }
}
