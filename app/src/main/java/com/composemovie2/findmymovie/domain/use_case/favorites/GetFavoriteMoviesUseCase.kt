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
                    id = entity.movieId,
                    title = entity.title,
                    overview = entity.overview,
                    posterPath = entity.posterPath, // Use the raw path from entity
                    backdropPath = null, // Not typically stored/needed for a favorites list item
                    releaseDate = entity.releaseDate,
                    voteAverage = entity.voteAverage,
                    voteCount = null, // Not available in FavoriteMovieEntity
                    popularity = null, // Not available in FavoriteMovieEntity
                    genres = null, // Not typically stored/needed for a favorites list item
                    status = null, // Not available in FavoriteMovieEntity
                    runtime = null, // Not available in FavoriteMovieEntity
                    imdbRating = entity.voteAverage, // Can use voteAverage as a stand-in if specific IMDb rating isn't stored
                    imdbVotes = null, // Not available in FavoriteMovieEntity
                    imdbId = null, // FavoriteMovieEntity stores TMDB ID (movieId), not IMDb string ID
                    budget = null, // Not available in FavoriteMovieEntity
                    revenue = null, // Not available in FavoriteMovieEntity
                    tagline = null, // Not available in FavoriteMovieEntity
                    year = entity.releaseDate?.substringBefore("-") // Existing year extraction logic is fine
                )
            }
        }
    }
}
