package com.composemovie2.findmymovie.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val movieId: Int, // TMDB Movie ID
    val title: String,
    val posterPath: String?, // Store path, construct full URL later
    val overview: String,
    val releaseDate: String?, // Or just year: String
    val voteAverage: Double?
    // Add other fields if needed for display in a favorites list
)
