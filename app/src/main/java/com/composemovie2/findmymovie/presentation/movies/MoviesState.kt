package com.composemovie2.findmymovie.presentation.movies

import com.composemovie2.findmymovie.domain.model.Movie

data class MoviesState(

    private val movies: List<Movie>? = emptyList(),
    private val isLoading: Boolean? = false,
    private val errorMsg: String? = ""


)