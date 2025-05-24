package com.composemovie2.findmymovie.domain.use_case.favorites

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import javax.inject.Inject

class AddFavoriteMovieUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend fun execute(favoriteMovie: FavoriteMovieEntity) {
        repository.addFavorite(favoriteMovie)
    }
}
