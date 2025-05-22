package com.composemovie2.findmymovie.presentation.movies

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.use_case.get_movies.GetMoviesUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
): ViewModel() {

    private val _state = mutableStateOf(MoviesState())
    val state : State<MoviesState> = _state

    private var job : Job?=null

    init {
        searchMovies("Batman")
    }

    private fun searchMovies(searchString:String){

        job?.cancel()

        job = getMoviesUseCase.executeGetMovies(searchString).onEach {

            when(it){

                is NetworkResult.Success -> {

                    _state.value = MoviesState(it.data?:emptyList())
                }
                is NetworkResult.Loading -> {
                    _state.value = MoviesState(isLoading=true)
                }
                is NetworkResult.Error -> {
                    _state.value = MoviesState(errorMsg = it.message?:"error")
                }


            }


        }.launchIn(viewModelScope)

    }

    fun onEvent(event: MoviesEvent){
        when(event){
            is MoviesEvent.Search-> {
                searchMovies(event.searchString)
            }
        }
    }

}