package com.composemovie2.findmymovie.data.remote

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto // OMDb
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbConfigurationDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenresResponseDto
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMovieDto // For TMDB movie details
import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbMoviesResponseDto
import com.composemovie2.findmymovie.util.Constants // Make sure this import is correct
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//https://omdbapi.com/?apikey=77836e9f&i=tt0118688  // Example OMDb call
//https://omdbapi.com/?apikey=77836e9f&s=batman // Example OMDb call


interface MovieAPI {

    // OMDb Endpoints (to be deprecated/removed)
    @GET(".") // Assuming OMDb calls are made to the base URL directly with parameters
    suspend fun getOMDbMovies(
        @Query("apikey") apiKey : String = Constants.OMDB_API_KEY, // Defaulting to existing OMDb key
        @Query("s") searchString : String
    ) : MoviesDto

    @GET(".") // Assuming OMDb calls are made to the base URL directly with parameters
    suspend fun getOMDbMovieDetail(
        @Query("apikey") apiKey:String = Constants.OMDB_API_KEY, // Defaulting to existing OMDb key
        @Query("i") imdbId:String,
    ) : MovieDetailDto

    // TMDB Endpoints
    // Note: For TMDB, the base URL in Retrofit setup will be Constants.TMDB_API_BASE_URL

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
        @Query("with_genres") genreId: String, // Comma-separated string of genre IDs
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc" // Default sort
    ): TmdbMoviesResponseDto

    // TMDB Movie Detail (TMDB uses its own integer movie ID)
    @GET("movie/{movie_id}")
    suspend fun getTmdbMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("append_to_response") appendToResponse: String? = null // e.g., "videos,credits"
    ): TmdbMovieDto // TmdbMovieDto is rich enough for details

    @GET("search/movie")
    suspend fun searchTmdbMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbMoviesResponseDto
}