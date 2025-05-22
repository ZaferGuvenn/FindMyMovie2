package com.composemovie2.findmymovie.presentation.movies

sealed class MoviesEvent() {

    data class Search(val searchString: String): MoviesEvent()

}