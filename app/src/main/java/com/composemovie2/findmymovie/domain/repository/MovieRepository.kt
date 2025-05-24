package com.composemovie2.findmymovie.domain.repository

import com.composemovie2.findmymovie.data.local.FavoriteMovieEntity 
import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProvidersResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbCountryDto // New
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProviderListResponseDto // New
import kotlinx.coroutines.flow.Flow 
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.model.PersonDetail
import com.composemovie2.findmymovie.domain.model.TVShow
import com.composemovie2.findmymovie.util.NetworkResult

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
    suspend fun getTmdbMovieWatchProviders(movieId: Int): TmdbWatchProvidersResponseDto

    // New methods for Settings
    suspend fun getTmdbConfigurationCountries(): List<TmdbCountryDto>
    suspend fun getTmdbAllMovieWatchProvidersList(watchRegion: String?): TmdbWatchProviderListResponseDto

    // Favorite Movie Operations
    fun getFavoriteMovies(): Flow<List<FavoriteMovieEntity>>
    fun isFavorite(movieId: Int): Flow<Boolean>
    suspend fun addFavorite(movie: FavoriteMovieEntity)
    suspend fun removeFavoriteById(movieId: Int)
    suspend fun getFavoriteMovieById(movieId: Int): FavoriteMovieEntity?

    suspend fun getMovies(page: Int): NetworkResult<List<Movie>>
    suspend fun getMovieDetails(movieId: Int): NetworkResult<MovieDetail>
    suspend fun searchMovies(query: String, page: Int): NetworkResult<List<Movie>>
    suspend fun getPersonDetails(personId: Int): NetworkResult<PersonDetail>
    suspend fun getPersonMovies(personId: Int): NetworkResult<List<Movie>>
    suspend fun getPersonTVShows(personId: Int): NetworkResult<List<TVShow>>
}
