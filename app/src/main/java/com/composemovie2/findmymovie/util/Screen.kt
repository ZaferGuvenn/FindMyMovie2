package com.composemovie2.findmymovie.util

sealed class Screen(val route: String) {
    object MovieScreen : Screen("movie_screen")
    object MovieDetailScreen : Screen("movie_detail_screen")
    object SettingsScreen : Screen("settings_screen")
    object PersonDetailScreen : Screen("person_detail_screen/{personId}") {
        fun createRoute(personId: Int) = "person_detail_screen/$personId"
    }
    object FindMovieScreen : Screen("find_movie_screen")
} 