package com.composemovie2.findmymovie.presentation.movie_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage // coil3
import com.composemovie2.findmymovie.domain.model.MovieDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.movieDetail?.title ?: "Movie Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMsg != null -> {
                    Text(
                        text = state.errorMsg ?: "An unknown error occurred.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                state.movieDetail != null -> {
                    val movie = state.movieDetail!!
                    MovieDetailContent(movie = movie)
                }
                else -> { // Should not happen if errorMsg is also null and not loading
                    Text(
                        text = "No movie details available.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailContent(movie: MovieDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = movie.poster,
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp) // Adjust height as desired
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit // Or ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Year: ${movie.year}", style = MaterialTheme.typography.bodyLarge)
            Text("Released: ${movie.released}", style = MaterialTheme.typography.bodyLarge)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Language: ${movie.language}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Rating: ${movie.imdbRating}/10 (TMDB)", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Genres: ${movie.genres}", style = MaterialTheme.typography.bodyMedium)


        Spacer(modifier = Modifier.height(16.dp))
        Text("Overview:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(movie.overview, style = MaterialTheme.typography.bodyLarge)

        // Placeholder for other details that are currently empty strings
        // These can be filled in Step 4 (Feature Enhancement)
        Spacer(modifier = Modifier.height(16.dp))
        if (movie.actors.isNotBlank()) Text("Actors: ${movie.actors}", style = MaterialTheme.typography.bodyMedium)
        if (movie.director.isNotBlank()) Text("Director: ${movie.director}", style = MaterialTheme.typography.bodyMedium)
        if (movie.awards.isNotBlank()) Text("Awards: ${movie.awards}", style = MaterialTheme.typography.bodyMedium)
        if (movie.country.isNotBlank()) Text("Country: ${movie.country}", style = MaterialTheme.typography.bodyMedium)

    }
}
