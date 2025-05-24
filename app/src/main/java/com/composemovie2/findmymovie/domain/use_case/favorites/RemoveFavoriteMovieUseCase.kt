package com.composemovie2.findmymovie.domain.use_case.favorites

import com.composemovie2.findmymovie.domain.repository.MovieRepository
import javax.inject.Inject

class RemoveFavoriteMovieUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend fun execute(movieId: Int) {
        repository.removeFavoriteById(movieId)
    }
}
