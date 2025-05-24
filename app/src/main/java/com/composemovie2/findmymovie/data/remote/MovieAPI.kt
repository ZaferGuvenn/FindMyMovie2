package com.composemovie2.findmymovie.data.remote

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto 
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProvidersResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbCountryDto // New
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbWatchProviderListResponseDto // New
import com.composemovie2.findmymovie.util.Constants 
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPI {

    // OMDb Endpoints (to be deprecated/removed)
    @GET(".") 
    suspend fun getOMDbMovies(
        @Query("apikey") apiKey : String = Constants.OMDB_API_KEY, 
        @Query("s") searchString : String
    ) : MoviesDto

    @GET(".") 
    suspend fun getOMDbMovieDetail(
        @Query("apikey") apiKey:String = Constants.OMDB_API_KEY, 
        @Query("i") imdbId:String,
    ) : MovieDetailDto

    // TMDB Endpoints
    @GET("configuration")
    suspend fun getTmdbConfiguration(
        @Query("api_key") apiKey: String
    ): TmdbConfigurationDto

    @GET("genre/movie/list")
    suspend fun getTmdbMovieGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): TmdbGenresResponseDto

    @GET("discover/movie")
    suspend fun discoverTmdbMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String, 
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc" 
    ): TmdbMoviesResponseDto

    @GET("movie/{movie_id}")
    suspend fun getTmdbMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("append_to_response") appendToResponse: String? = "credits,videos"
    ): TmdbMovieDto 

    @GET("search/movie")
    suspend fun searchTmdbMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbMoviesResponseDto

    @GET("movie/popular") 
    suspend fun getTmdbPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null 
    ): TmdbMoviesResponseDto

    @GET("movie/now_playing") 
    suspend fun getTmdbNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null
    ): TmdbMoviesResponseDto

    @GET("movie/upcoming") 
    suspend fun getTmdbUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null
    ): TmdbMoviesResponseDto

    @GET("movie/{movie_id}/watch/providers") 
    suspend fun getTmdbMovieWatchProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): TmdbWatchProvidersResponseDto

    @GET("configuration/countries") // New
    suspend fun getTmdbConfigurationCountries(
        @Query("api_key") apiKey: String
    ): List<TmdbCountryDto> 

    @GET("watch/providers/movie") // New
    suspend fun getTmdbAllMovieWatchProvidersList(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("watch_region") watchRegion: String? = null 
    ): TmdbWatchProviderListResponseDto
}
