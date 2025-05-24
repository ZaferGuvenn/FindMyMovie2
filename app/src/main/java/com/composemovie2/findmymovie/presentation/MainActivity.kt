package com.composemovie2.findmymovie.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.composemovie2.findmymovie.presentation.movie_detail.MovieDetailScreen
import com.composemovie2.findmymovie.presentation.movies.views.MoviesScreen
import com.composemovie2.findmymovie.presentation.settings.SettingsScreen // New import
import com.composemovie2.findmymovie.presentation.ui.theme.FindMyMovieTheme
import com.composemovie2.findmymovie.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FindMyMovieTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.MovieScreen.route
                    ) {
                        composable(route = Screen.MovieScreen.route) {
                            MoviesScreen(navController = navController)
                        }
                        composable(
                            route = Screen.MovieDetailScreen.route + "/{movieId}",
                            arguments = listOf(
                                navArgument(name = "movieId") { type = NavType.StringType }
                            )
                        ) {
                            MovieDetailScreen(navController = navController)
                        }
                        composable("SettingsScreen") { // New Route
                            SettingsScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
