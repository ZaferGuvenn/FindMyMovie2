package com.composemovie2.findmymovie.presentation.movies.views

import androidx.compose.foundation.background
// ... other necessary imports ...
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composemovie2.findmymovie.domain.model.Genre // Import Genre model
import com.composemovie2.findmymovie.presentation.movies.MoviesEvent
import com.composemovie2.findmymovie.presentation.movies.MoviesViewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.Movie
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    navController: NavController,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {
    val state by moviesViewModel.state // Observe state directly
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("All") } // For client-side type filtering

    // This local 'selectedCategory' will now track the ID of the selected TMDB genre for UI purposes
    var selectedGenreId by remember { mutableStateOf<String?>(null) }


    // Effect to trigger search when searchQuery changes (debounced)
    LaunchedEffect(searchQuery) {
        // Only trigger search if query is not blank, or to reset to popular if it becomes blank
        if (searchQuery.isNotBlank() && searchQuery.length >= 2) { // Trigger search after 2 chars
            delay(500) // Debounce
            moviesViewModel.onEvent(MoviesEvent.SearchByQuery(searchQuery))
        } else if (searchQuery.isBlank()) {
            // If search query is cleared by user, load popular movies
            // This behavior can be tuned. For now, let's make it explicit via a button or clear action.
            // Or, if user clears search, we could revert to selected genre or popular.
            // Let's assume clearing search means they want to see popular/default for now.
            // The ViewModel's SearchByQuery event handles blank query by loading popular.
            moviesViewModel.onEvent(MoviesEvent.SearchByQuery(""))
        }
    }
    
    // Default "All" / "Popular" Tab. This is a conceptual tab, not a real genre from API.
    val popularMoviesPseudoGenre = Genre(id = -1, name = "Popular")


    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Find My Movie (TMDB)", fontWeight = FontWeight.Bold) }, // Updated title
                    actions = {
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter by Type")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                // Categories / Genres from TMDB
                if (state.isLoadingGenres) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else if (state.genresErrorMsg.isNotBlank()) {
                    Text(
                        state.genresErrorMsg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    ScrollableTabRow(
                        // Determine selected tab index based on selectedGenreId
                        selectedTabIndex = if (selectedGenreId == null) 0 else state.genres.indexOfFirst { it.id.toString() == selectedGenreId } + 1,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 0.dp // Adjust as needed
                    ) {
                        // "Popular" Tab (Conceptual)
                        Tab(
                            selected = selectedGenreId == null,
                            onClick = {
                                selectedGenreId = null
                                moviesViewModel.onEvent(MoviesEvent.LoadPopularMovies)
                            },
                            text = { Text(popularMoviesPseudoGenre.name) }
                        )
                        // Actual Genre Tabs
                        state.genres.forEach { genre ->
                            Tab(
                                selected = selectedGenreId == genre.id.toString(),
                                onClick = {
                                    selectedGenreId = genre.id.toString()
                                    moviesViewModel.onEvent(MoviesEvent.SearchByGenre(genre.id.toString()))
                                },
                                text = { Text(genre.name) }
                            )
                        }
                    }
                }


                // Search Bar (remains largely the same)
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it }, // LaunchedEffect handles the event dispatch
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search movies by title...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) { // Clearing search handled by LaunchedEffect
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
                // Filter Chip for Type (client-side, remains same)
                 if (selectedType != "All") {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filter: $selectedType",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { selectedType = "All" },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear filter",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.errorMsg.isNotBlank() -> {
                    Card( 
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.errorMsg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                state.movies.isEmpty() && !state.isLoading -> { // Show message if no movies and not loading
                    Column( 
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Movie, // Changed icon
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedGenreId != null)
                                "No movies found for this genre."
                            else if (searchQuery.isNotBlank())
                                "No movies found for your search."
                            else
                                "No movies to display. Try selecting a genre or searching.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid( // Client-side type filtering applied here
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            state.movies.filter { movie ->
                                selectedType == "All" || movie.type.equals(selectedType, ignoreCase = true)
                                // Movie.type from TMDB is hardcoded to "movie" for now by TmdbMovieMapper.
                                // If TMDB provides actual types and mapper is updated, this filter will work.
                                // For now, it will mostly show all if selectedType is "movie" or "All".
                            }
                        ) { movie ->
                            MovieListRow(
                                movie = movie,
                                onItemClick = {
                                    // IMPORTANT: movie.imdbId now holds TMDB's INTEGER ID as a String.
                                    // Navigation to MovieDetailScreen needs to pass this integer ID.
                                    // The MovieDetailScreen and its ViewModel will use this TMDB int ID.
                                    navController.navigate("movie_detail_screen/${movie.imdbId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter Dialog for Type (remains the same)
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Type") },
            text = {
                Column {
                    // TMDB mainly gives "movie" or "tv". OMDb had "episode".
                    // This list might need adjustment based on actual types from TMDB if we differentiate.
                    // For now, our Movie.type is hardcoded to "movie".
                    listOf("All", "movie", "series", "episode").forEach { type -> 
                        Row(
                            Modifier.fillMaxWidth().clickable { selectedType = type; showFilterDialog = false }.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedType == type, onClick = { selectedType = type; showFilterDialog = false })
                            Spacer(Modifier.width(8.dp))
                            Text(type.replaceFirstChar { it.uppercase() }) // Capitalize
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFilterDialog = false }) { Text("Close") } }
        )
    }
}
