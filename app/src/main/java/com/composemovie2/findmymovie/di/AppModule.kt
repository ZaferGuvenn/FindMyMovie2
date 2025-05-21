package com.composemovie2.findmymovie.di

import com.composemovie2.findmymovie.data.remote.MovieAPI
import com.composemovie2.findmymovie.data.repository.MovieRepositoryImpl
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants.API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideMovieAPI() : MovieAPI {

        return Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(movieApi: MovieAPI) : MovieRepository{

        return MovieRepositoryImpl(movieAPI = movieApi)
    }

}