package com.composemovie2.findmymovie.domain.repository

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity // New import
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import kotlinx.coroutines.flow.Flow // New import

interface MovieRepository {
    // OMDb methods (to be removed or fully replaced)
    suspend fun getMoviesDto(searchString: String): MoviesDto 
    suspend fun getMovieDetailDto(imdbId: String): MovieDetailDto 

    // TMDB methods
    suspend fun getTmdbConfiguration(): TmdbConfigurationDto
    suspend fun getTmdbMovieGenres(): TmdbGenresResponseDto 
    suspend fun discoverTmdbMoviesByGenre(genreId: String, page: Int): TmdbMoviesResponseDto 
    suspend fun searchTmdbMovies(query: String, page: Int): TmdbMoviesResponseDto 
    suspend fun getTmdbMovieDetails(movieId: Int): TmdbMovieDto 
    suspend fun getTmdbPopularMovies(page: Int): TmdbMoviesResponseDto
    suspend fun getTmdbNowPlayingMovies(page: Int): TmdbMoviesResponseDto
    suspend fun getTmdbUpcomingMovies(page: Int): TmdbMoviesResponseDto

    // Favorite Movie Operations
    fun getFavoriteMovies(): Flow<List<FavoriteMovieEntity>>
    fun isFavorite(movieId: Int): Flow<Boolean>
    suspend fun addFavorite(movie: FavoriteMovieEntity)
    suspend fun removeFavoriteById(movieId: Int)
    suspend fun getFavoriteMovieById(movieId: Int): FavoriteMovieEntity?
}
