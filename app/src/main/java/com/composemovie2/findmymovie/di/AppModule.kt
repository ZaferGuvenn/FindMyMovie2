package com.composemovie2.findmymovie.di

import android.app.Application // Required for Room.databaseBuilder context
import androidx.room.Room
import com.composemovie2.findmymovie.data.local.MovieDao
import com.composemovie2.findmymovie.data.local.MovieDatabase
// ... other imports from AppModule ...
import com.composemovie2.findmymovie.data.remote.MovieAPI
import com.composemovie2.findmymovie.data.repository.MovieRepositoryImpl
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
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
    fun provideMovieApi(): MovieAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.TMDB_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieAPI::class.java)
    }

    // This provider needs to be updated if MovieRepositoryImpl constructor changes
    @Provides
    @Singleton
    fun provideMovieRepository(
        api: MovieAPI, 
        movieDao: MovieDao // Add MovieDao as a dependency
    ): MovieRepository {
        // MovieRepositoryImpl will need to be updated to accept MovieDao
        return MovieRepositoryImpl(movieAPI = api, movieDao = movieDao) 
    }

    // New providers for Room Database and DAO
    @Provides
    @Singleton
    fun provideMovieDatabase(app: Application): MovieDatabase {
        return Room.databaseBuilder(
            app,
            MovieDatabase::class.java,
            MovieDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao {
        return database.movieDao()
    }
}
