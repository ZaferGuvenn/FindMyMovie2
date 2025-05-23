package com.composemovie2.findmymovie.presentation.movies

import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.Genre // Import Genre

data class MoviesState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String = "",

    val genres: List<Genre> = emptyList(),
    val isLoadingGenres: Boolean = false,
    val genresErrorMsg: String = ""
)
