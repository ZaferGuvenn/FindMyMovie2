package com.composemovie2.findmymovie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovieEntity::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
   abstract fun movieDao(): MovieDao

   companion object {
       const val DATABASE_NAME = "movie_database"
   }
}
