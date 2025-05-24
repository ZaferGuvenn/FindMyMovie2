package com.composemovie2.findmymovie.presentation.person_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.PersonDetail
import com.composemovie2.findmymovie.domain.model.TVShow
import com.composemovie2.findmymovie.domain.repository.MovieRepository
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
            _state.value = _state.value.copy(isLoading = true)
            try {
                val personDetail = repository.getPersonDetails(personId)
                val knownForMovies = repository.getPersonMovies(personId)
                val knownForTVShows = repository.getPersonTVShows(personId)
                
                _state.value = _state.value.copy(
                    personDetail = personDetail,
                    knownForMovies = knownForMovies,
                    knownForTVShows = knownForTVShows,
                    isLoading = false,
                    errorMsg = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = e.message ?: "An error occurred"
                )
            }
        }
    }
} 