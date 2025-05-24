package com.composemovie2.findmymovie.presentation.person_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.PersonDetail
import com.composemovie2.findmymovie.domain.model.TVShow
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PersonDetailState())
    val state: StateFlow<PersonDetailState> = _state.asStateFlow()

    fun loadPersonDetails(personId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMsg = null) // Clear previous errors
            try {
                val personDetailResult = repository.getPersonDetails(personId)
                val knownForMoviesResult = repository.getPersonMovies(personId)
                val knownForTVShowsResult = repository.getPersonTVShows(personId)

                var finalPersonDetail: PersonDetail? = null
                var finalKnownForMovies: List<Movie> = emptyList()
                var finalKnownForTVShows: List<TVShow> = emptyList()
                val errorMessages = mutableListOf<String>()

                when (personDetailResult) {
                    is NetworkResult.Success -> finalPersonDetail = personDetailResult.data
                    is NetworkResult.Error -> errorMessages.add(personDetailResult.message ?: "Error fetching person details")
                    is NetworkResult.Loading -> { /* Handled by initial isLoading=true */ }
                }

                when (knownForMoviesResult) {
                    is NetworkResult.Success -> finalKnownForMovies = knownForMoviesResult.data ?: emptyList()
                    is NetworkResult.Error -> errorMessages.add(knownForMoviesResult.message ?: "Error fetching known for movies")
                    is NetworkResult.Loading -> { /* Handled */ }
                }

                when (knownForTVShowsResult) {
                    is NetworkResult.Success -> finalKnownForTVShows = knownForTVShowsResult.data ?: emptyList()
                    is NetworkResult.Error -> errorMessages.add(knownForTVShowsResult.message ?: "Error fetching known for TV shows")
                    is NetworkResult.Loading -> { /* Handled */ }
                }
                
                _state.value = _state.value.copy(
                    personDetail = finalPersonDetail,
                    knownForMovies = finalKnownForMovies,
                    knownForTVShows = finalKnownForTVShows,
                    isLoading = false,
                    errorMsg = if (errorMessages.isNotEmpty()) errorMessages.joinToString("\n") else null
                )

            } catch (e: Exception) { // Catch any unexpected exceptions from the repository calls themselves
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
} 