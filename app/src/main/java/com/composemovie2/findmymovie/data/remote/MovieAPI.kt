package com.composemovie2.findmymovie.data.remote

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto
import com.composemovie2.findmymovie.data.remote.dto.MoviesDto
import com.composemovie2.findmymovie.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

//https://omdbapi.com/?apikey=77836e9f&i=tt0118688
//https://omdbapi.com/?apikey=77836e9f&s=batman


interface MovieAPI {

    @GET(".")
    suspend fun getMovies(
        @Query("s") searchString: String,
        @Query("apikey") apiKey:String=API_KEY
    ) : MoviesDto

    @GET(".")
    suspend fun getMovieDetail(

        @Query("apikey") apiKey:String =API_KEY,
        @Query("i") imdbId:String,

    ) : MovieDetailDto

}