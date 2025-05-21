package com.composemovie2.findmymovie.domain.repository

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto

interface MovieRepository {

    suspend fun getMoviesDto(searchString: String) : MoviesDto
    suspend fun getMovieDetailDto(imdbId: String) : MovieDetailDto

}