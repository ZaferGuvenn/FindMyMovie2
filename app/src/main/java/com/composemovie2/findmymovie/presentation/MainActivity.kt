package com.composemovie2.findmymovie.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composemovie2.findmymovie.presentation.movies.views.MoviesScreen
import com.composemovie2.findmymovie.presentation.theme.FindMyMovie2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindMyMovie2Theme {
                Surface(color = MaterialTheme.colorScheme.background){
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->



                        MainScreen(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun MainScreen(name: String, modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController, "MoviesScreen"){

        composable("MoviesScreen") {

            MoviesScreen(navController)
        }

        composable("MovieDetailScreen"){

        }

    }


}