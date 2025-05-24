package com.composemovie2.findmymovie.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun addFavorite(movie: FavoriteMovieEntity)

   @Delete
   suspend fun removeFavorite(movie: FavoriteMovieEntity)

   @Query("SELECT * FROM favorite_movies ORDER BY title ASC")
   fun getFavoriteMovies(): Flow<List<FavoriteMovieEntity>>

   @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE movieId = :movieId LIMIT 1)")
   fun isFavorite(movieId: Int): Flow<Boolean>
   
   @Query("SELECT * FROM favorite_movies WHERE movieId = :movieId LIMIT 1")
   suspend fun getFavoriteMovieById(movieId: Int): FavoriteMovieEntity? // For quick check or removal by ID
   
   @Query("DELETE FROM favorite_movies WHERE movieId = :movieId")
   suspend fun removeFavoriteById(movieId: Int)
}
