package com.composemovie2.findmymovie.presentation.movies

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.model.Genre // Import Genre
import com.composemovie2.findmymovie.domain.use_case.get_genres.GetGenresUseCase // Import GetGenresUseCase
import com.composemovie2.findmymovie.domain.use_case.get_movies.GetMoviesUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getGenresUseCase: GetGenresUseCase // Inject GetGenresUseCase
): ViewModel() {

    private val _state = mutableStateOf(MoviesState())
    val state : State<MoviesState> = _state

    private var moviesJob : Job? = null
    private var genresJob : Job? = null

    init {
        fetchGenres()
        onEvent(MoviesEvent.LoadPopularMovies) // Use the new event for initial load
    }

    private fun fetchGenres() {
        genresJob?.cancel()
        genresJob = getGenresUseCase.executeGetGenres().onEach { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        genres = result.data ?: emptyList(),
                        isLoadingGenres = false,
                        genresErrorMsg = ""
                    )
                }
                is NetworkResult.Loading -> {
                    _state.value = _state.value.copy(isLoadingGenres = true, genresErrorMsg = "")
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingGenres = false,
                        genresErrorMsg = result.message ?: "Error fetching genres"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadMovies(genreId: String? = null, searchQuery: String? = null, page: Int = 1) {
        moviesJob?.cancel()
        moviesJob = getMoviesUseCase.executeGetMovies(genreId = genreId, searchQuery = searchQuery, page = page)
            .onEach { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _state.value = _state.value.copy(
                            movies = result.data ?: emptyList(),
                            isLoading = false,
                            errorMsg = ""
                        )
                    }
                    is NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, errorMsg = "")
                    }
                    is NetworkResult.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMsg = result.message ?: "Error loading movies"
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.SearchByQuery -> {
                if (event.searchQuery.isBlank()) {
                    loadMovies(searchQuery = "popular") // Default to popular if query is blank
                } else {
                    loadMovies(searchQuery = event.searchQuery)
                }
            }
            is MoviesEvent.SearchByGenre -> {
                loadMovies(genreId = event.genreId)
            }
            is MoviesEvent.LoadPopularMovies -> {
                // TMDB 'discover' endpoint with sort_by=popularity.desc is better for popular.
                // For now, GetMoviesUseCase uses search if query is not blank.
                // To implement true "popular", GetMoviesUseCase might need another parameter like 'sortBy'.
                // As a simplification, we'll use a keyword search for "popular".
                // Or, call discover with no genre and no query, if GetMoviesUseCase supports it.
                // The current GetMoviesUseCase requires either genreId or searchQuery.
                // So, let's use searchQuery="popular" as a stand-in.
                loadMovies(searchQuery = "popular")
            }
        }
    }
}
