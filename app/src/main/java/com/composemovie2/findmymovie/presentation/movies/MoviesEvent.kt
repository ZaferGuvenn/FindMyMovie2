package com.composemovie2.findmymovie.presentation.movies

sealed class MoviesEvent {
    data class SearchByQuery(val searchQuery: String) : MoviesEvent()
    data class SearchByGenre(val genreId: String) : MoviesEvent()
    object LoadPopularMovies : MoviesEvent()
    object LoadNowPlayingMovies : MoviesEvent()
    object LoadUpcomingMovies : MoviesEvent()
    object LoadFavoriteMovies : MoviesEvent() // New
}
