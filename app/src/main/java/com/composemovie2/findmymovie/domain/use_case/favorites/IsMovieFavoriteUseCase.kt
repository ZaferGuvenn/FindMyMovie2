package com.composemovie2.findmymovie.domain.use_case.favorites

import com.composemovie2.findmymovie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsMovieFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    fun execute(movieId: Int): Flow<Boolean> {
        return repository.isFavorite(movieId)
    }
}
