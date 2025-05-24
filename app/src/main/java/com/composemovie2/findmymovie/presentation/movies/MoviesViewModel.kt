package com.composemovie2.findmymovie.presentation.movies

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.model.Genre 
import com.composemovie2.findmymovie.domain.use_case.get_genres.GetGenresUseCase 
import com.composemovie2.findmymovie.domain.use_case.get_movies.GetMoviesUseCase
import com.composemovie2.findmymovie.domain.use_case.favorites.GetFavoriteMoviesUseCase
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.distinctUntilChanged 
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
): ViewModel() {

    private val _state = mutableStateOf(MoviesState())
    val state : State<MoviesState> = _state

    private var moviesJob : Job? = null
    private var genresJob : Job? = null
    private var favoritesJob: Job? = null 

    init {
        fetchGenres()
        onEvent(MoviesEvent.LoadPopularMovies)
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

    fun retryFetchGenres() { // New public method
        fetchGenres()
    }

    private fun loadMovies(
        genreId: String? = null, 
        searchQuery: String? = null, 
        fetchPopular: Boolean = false,
        fetchNowPlaying: Boolean = false,
        fetchUpcoming: Boolean = false,
        page: Int = 1
    ) {
        favoritesJob?.cancel() 
        moviesJob?.cancel()
        moviesJob = getMoviesUseCase.executeGetMovies(
            genreId, searchQuery, fetchPopular, fetchNowPlaying, fetchUpcoming, page
        ).onEach { result -> 
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
    
    private fun loadFavoriteMovies() {
        moviesJob?.cancel() 
        favoritesJob?.cancel()
        favoritesJob = getFavoriteMoviesUseCase.execute()
            .distinctUntilChanged() 
            .onEach { movies ->
                _state.value = _state.value.copy(
                    movies = movies,
                    isLoading = false, 
                    errorMsg = "",
                    currentListTitle = "Favorites",
                    isGenreListVisible = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: MoviesEvent) {
        // The prompt suggested:
        // if (event !is MoviesEvent.SearchByQuery) { lastLoadMoviesEvent = event }
        // However, to avoid complexity, retry logic is handled in UI by re-dispatching specific events.
        when (event) {
            is MoviesEvent.SearchByQuery -> {
                val title = if (event.searchQuery.isBlank()) "Popular" else "Search: \"${event.searchQuery}\""
                _state.value = _state.value.copy(currentListTitle = title, isGenreListVisible = true)
                if (event.searchQuery.isBlank()) {
                    loadMovies(fetchPopular = true) 
                } else {
                    loadMovies(searchQuery = event.searchQuery)
                }
            }
            is MoviesEvent.SearchByGenre -> {
                val genreName = _state.value.genres.find { it.id.toString() == event.genreId }?.name ?: "Genre"
                _state.value = _state.value.copy(currentListTitle = genreName, isGenreListVisible = true)
                loadMovies(genreId = event.genreId)
            }
            is MoviesEvent.LoadPopularMovies -> {
                _state.value = _state.value.copy(currentListTitle = "Popular", isGenreListVisible = true)
                loadMovies(fetchPopular = true)
            }
            is MoviesEvent.LoadNowPlayingMovies -> {
                _state.value = _state.value.copy(currentListTitle = "Now Playing", isGenreListVisible = true)
                loadMovies(fetchNowPlaying = true)
            }
            is MoviesEvent.LoadUpcomingMovies -> {
                _state.value = _state.value.copy(currentListTitle = "Upcoming", isGenreListVisible = true)
                loadMovies(fetchUpcoming = true)
            }
            is MoviesEvent.LoadFavoriteMovies -> { 
                loadFavoriteMovies()
            }
        }
    }
}
