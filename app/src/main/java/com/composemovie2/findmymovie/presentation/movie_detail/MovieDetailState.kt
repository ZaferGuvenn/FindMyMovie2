package com.composemovie2.findmymovie.presentation.movie_detail

import com.composemovie2.findmymovie.domain.model.MovieDetail

data class MovieDetailState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)
