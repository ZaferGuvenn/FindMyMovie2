package com.composemovie2.findmymovie.presentation.person_detail

import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.PersonDetail
import com.composemovie2.findmymovie.domain.model.TVShow

data class PersonDetailState(
    val personDetail: PersonDetail? = null,
    val knownForMovies: List<Movie> = emptyList(),
    val knownForTVShows: List<TVShow> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
) 