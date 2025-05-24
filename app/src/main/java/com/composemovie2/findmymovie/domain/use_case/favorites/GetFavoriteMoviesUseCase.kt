package com.composemovie2.findmymovie.domain.use_case.favorites

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity
import com.composemovie2.findmymovie.domain.model.Movie // We need to map Entity to Domain Model
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants // For image base URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    // This UseCase should map FavoriteMovieEntity to domain model Movie for UI consistency
    fun execute(): Flow<List<Movie>> {
        return repository.getFavoriteMovies().map { entities ->
            entities.map { entity ->
                Movie(
                    imdbId = entity.movieId.toString(), // TMDB ID
                    title = entity.title,
                    poster = entity.posterPath?.let { "${Constants.TMDB_IMAGE_BASE_URL}${Constants.DEFAULT_POSTER_SIZE}$it" } ?: "",
                    year = entity.releaseDate?.substringBefore("-") ?: "N/A",
                    type = "movie" // Assuming favorites are movies. Could add 'type' to entity if needed.
                )
            }
        }
    }
}
