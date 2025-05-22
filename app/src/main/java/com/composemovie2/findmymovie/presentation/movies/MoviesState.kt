package com.composemovie2.findmymovie.presentation.movies

import com.composemovie2.findmymovie.domain.model.Movie

data class MoviesState(

     val movies: List<Movie> = emptyList(),
     val isLoading: Boolean = false,
     val errorMsg: String = ""


)