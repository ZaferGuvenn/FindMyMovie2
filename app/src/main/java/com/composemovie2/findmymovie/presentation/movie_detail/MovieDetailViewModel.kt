package com.composemovie2.findmymovie.presentation.movie_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.use_case.get_movie_detail.GetMovieDetailUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {

    private val _state = mutableStateOf<MovieDetailState>(MovieDetailState())
    val state: State<MovieDetailState> = _state

    fun getMovieDetail(imdbId: String) {
        getMovieDetailUseCase.executeMovieDetail(imdbId).onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _state.value = MovieDetailState(movieDetail = result.data)
                }
                is NetworkResult.Loading -> {
                    _state.value = MovieDetailState(isLoading = true)
                }
                is NetworkResult.Error -> {
                    _state.value = MovieDetailState(errorMsg = result.message ?: "An error occurred")
                }
            }
        }.launchIn(viewModelScope)
    }
}

data class MovieDetailState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val errorMsg: String = ""
) 