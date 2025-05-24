package com.composemovie2.findmymovie.presentation.findmovie

// import androidx.compose.foundation.layout.Arrangement // Removed duplicate
import androidx.compose.foundation.layout.Arrangement // Kept one
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Movie // Added for consistency in RecommendedMovieCard placeholder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// import androidx.compose.runtime.mutableStateOf // Replaced by ViewModel state
// import androidx.compose.runtime.remember // Replaced by ViewModel state
// import androidx.compose.runtime.setValue // Replaced by ViewModel state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter // Added import
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.res.painterResource // No longer used directly for icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.composemovie2.findmymovie.R
import com.composemovie2.findmymovie.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindMovieScreen(
    navController: NavController,
    viewModel: FindMovieViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Your Movie") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Describe the kind of movie you're looking for...") },
                minLines = 4,
                isError = uiState.error?.startsWith("Please enter a description") == true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.getRecommendations(uiState.inputText) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Recommendations")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (uiState.recommendations.isEmpty() && !uiState.isLoading && uiState.error == null) {
                Text(
                    text = "Enter a description above and tap 'Get Recommendations' to see results.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.recommendations) { movie ->
                        RecommendedMovieCard(
                            movie = movie,
                            onClick = {
                                movie.tmdbId?.let { tmdbId ->
                                    // MovieDetailScreen expects movieId as String (TMDB ID)
                                    navController.navigate(Screen.MovieDetailScreen.route + "/${tmdbId}")
                                } ?: run {
                                    android.widget.Toast.makeText(context, "Details not available for this movie.", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedMovieCard(movie: RecommendedMovieItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (!movie.posterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .size(width = 100.dp, height = 150.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(image = Icons.Outlined.BrokenImage) // Updated
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Movie, // This is correct for Icon
                        contentDescription = "No image available",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title + if (movie.year != null) " (${movie.year})" else "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.reason,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
