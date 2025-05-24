package com.composemovie2.findmymovie.di

import android.app.Application 
import android.content.Context // New import for UserPreferencesRepository provider
import androidx.room.Room
import com.composemovie2.findmymovie.data.local.MovieDao
import com.composemovie2.findmymovie.data.local.MovieDatabase
import com.composemovie2.findmymovie.data.preferences.UserPreferencesRepository // New import
import com.composemovie2.findmymovie.data.remote.api.MovieAPI // Updated import
import com.composemovie2.findmymovie.data.repository.MovieRepositoryImpl
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext // New import for UserPreferencesRepository provider
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.composemovie2.findmymovie.BuildConfig // For GEMINI_API_KEY
import com.composemovie2.findmymovie.data.remote.GeminiAiService


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
            .create(com.composemovie2.findmymovie.data.remote.api.MovieAPI::class.java) // Updated create call
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: MovieAPI, 
        movieDao: MovieDao 
    ): MovieRepository {
        return MovieRepositoryImpl(movieAPI = api, movieDao = movieDao) 
    }

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

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository { // New provider
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideGeminiAiService(): GeminiAiService {
        return GeminiAiService(apiKey = BuildConfig.GEMINI_API_KEY)
    }
}
