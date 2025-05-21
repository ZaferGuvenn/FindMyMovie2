package com.composemovie2.findmymovie.data.repository

import com.composemovie2.findmymovie.data.remote.MovieAPI
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieAPI: MovieAPI
): MovieRepository{
    override suspend fun getMoviesDto(searchString: String): MoviesDto {
        return movieAPI.getMovies(searchString = searchString)
    }

    override suspend fun getMovieDetailDto(imdbId: String): MovieDetailDto {
        return movieAPI.getMovieDetail(imdbId = imdbId)
    }
}