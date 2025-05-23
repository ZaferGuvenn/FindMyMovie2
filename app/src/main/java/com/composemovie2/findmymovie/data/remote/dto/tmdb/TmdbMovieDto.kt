package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbMovieDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path") // Was already here, ensure it's present
    val backdropPath: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    
    // For movie lists (discover/search), it's "genre_ids": List<Int>?
    // For movie details endpoint, it's "genres": List<TmdbGenreDto>?
    // To handle both, make genreIds nullable and add genres. One will be populated.
    @SerializedName("genre_ids") 
    val genreIds: List<Int>?, 
    @SerializedName("genres") // From Movie Details endpoint
    val genresFull: List<TmdbGenreDto>?,

    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("video") // From movie lists, indicates if videos exist generally
    val video: Boolean?,

    // New fields for Movie Details response
    @SerializedName("runtime")
    val runtime: Int?, // in minutes
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("status")
    val status: String?, // e.g., "Released", "Post Production"
    
    // Appended responses
    @SerializedName("credits")
    val credits: TmdbCreditsDto?,
    @SerializedName("videos")
    val videos: TmdbVideosResponseDto?
)
