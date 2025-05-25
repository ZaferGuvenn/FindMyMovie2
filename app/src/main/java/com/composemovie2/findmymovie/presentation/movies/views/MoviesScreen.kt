package com.composemovie2.findmymovie.presentation.movies.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow 
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composemovie2.findmymovie.domain.model.Genre 
import com.composemovie2.findmymovie.presentation.movies.MoviesEvent
import com.composemovie2.findmymovie.presentation.movies.MoviesViewModel
import com.composemovie2.findmymovie.presentation.components.CommonErrorView 
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.Movie
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    navController: NavController,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {
    val state by moviesViewModel.state 
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("All") } 

    val popularMoviesPseudoGenre = Genre(id = -1, name = "Popular")
    val nowPlayingPseudoGenre = Genre(id = -2, name = "Now Playing")
    val upcomingPseudoGenre = Genre(id = -3, name = "Upcoming")
    val favoritesPseudoGenre = Genre(id = -4, name = "Favorites") 
    
    var selectedPseudoGenreType by remember { mutableStateOf(popularMoviesPseudoGenre.id.toString()) } 


    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && searchQuery.length >= 2) { 
            delay(500) 
            moviesViewModel.onEvent(MoviesEvent.SearchByQuery(searchQuery))
            selectedPseudoGenreType = "null"
        } else if (searchQuery.isBlank()) {
            selectedPseudoGenreType = popularMoviesPseudoGenre.id.toString()
            moviesViewModel.onEvent(MoviesEvent.LoadPopularMovies)
        }
    }
    
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(state.currentListTitle, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) }, 
                    actions = {
                        IconButton(onClick = { navController.navigate("SettingsScreen") }) { // New Navigation
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                        }
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
                if (state.isLoadingGenres && state.isGenreListVisible) { 
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else if (state.genresErrorMsg.isNotBlank() && state.isGenreListVisible) { 
                    Text(
                        state.genresErrorMsg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else if (state.isGenreListVisible) { 
                     ScrollableTabRow(
                        selectedTabIndex = when (selectedPseudoGenreType) {
                            popularMoviesPseudoGenre.id.toString() -> 0
                            nowPlayingPseudoGenre.id.toString() -> 1
                            upcomingPseudoGenre.id.toString() -> 2
                            favoritesPseudoGenre.id.toString() -> 3 
                            else -> state.genres.indexOfFirst { it.id.toString() == selectedPseudoGenreType } + 4 
                        }.coerceAtLeast(0), 
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 0.dp 
                    ) {
                        Tab(selected = selectedPseudoGenreType == popularMoviesPseudoGenre.id.toString(), onClick = { selectedPseudoGenreType = popularMoviesPseudoGenre.id.toString(); moviesViewModel.onEvent(MoviesEvent.LoadPopularMovies); searchQuery = "" }, text = { Text(popularMoviesPseudoGenre.name) })
                        Tab(selected = selectedPseudoGenreType == nowPlayingPseudoGenre.id.toString(), onClick = { selectedPseudoGenreType = nowPlayingPseudoGenre.id.toString(); moviesViewModel.onEvent(MoviesEvent.LoadNowPlayingMovies); searchQuery = "" }, text = { Text(nowPlayingPseudoGenre.name) })
                        Tab(selected = selectedPseudoGenreType == upcomingPseudoGenre.id.toString(), onClick = { selectedPseudoGenreType = upcomingPseudoGenre.id.toString(); moviesViewModel.onEvent(MoviesEvent.LoadUpcomingMovies); searchQuery = "" }, text = { Text(upcomingPseudoGenre.name) })
                        Tab(selected = selectedPseudoGenreType == favoritesPseudoGenre.id.toString(), onClick = { selectedPseudoGenreType = favoritesPseudoGenre.id.toString(); moviesViewModel.onEvent(MoviesEvent.LoadFavoriteMovies); searchQuery = "" }, text = { Text(favoritesPseudoGenre.name) })
                        state.genres.forEach { genre -> 
                            Tab(selected = selectedPseudoGenreType == genre.id.toString(), onClick = { selectedPseudoGenreType = genre.id.toString(); moviesViewModel.onEvent(MoviesEvent.SearchByGenre(genre.id.toString())); searchQuery = "" }, text = { Text(genre.name) })
                        }
                    }
                } 

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    TextField(value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search movies by title...") }, leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") }, trailingIcon = { if (searchQuery.isNotEmpty()) { IconButton(onClick = { searchQuery = "" }) { Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear") } } }, colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), singleLine = true)
                }
                 if (selectedType != "All") {
                    Surface(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primaryContainer) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Filter: $selectedType", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { selectedType = "All" }, modifier = Modifier.size(16.dp)) { Icon(imageVector = Icons.Default.Close, contentDescription = "Clear filter", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp)) }
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
                state.isLoading -> { 
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMsg.isNotBlank() -> { 
                    CommonErrorView(
                        message = state.errorMsg,
                        onRetry = { 
                            val currentSelectedTabType = selectedPseudoGenreType 
                            when (currentSelectedTabType) {
                                popularMoviesPseudoGenre.id.toString() -> moviesViewModel.onEvent(MoviesEvent.LoadPopularMovies)
                                nowPlayingPseudoGenre.id.toString() -> moviesViewModel.onEvent(MoviesEvent.LoadNowPlayingMovies)
                                upcomingPseudoGenre.id.toString() -> moviesViewModel.onEvent(MoviesEvent.LoadUpcomingMovies)
                                favoritesPseudoGenre.id.toString() -> moviesViewModel.onEvent(MoviesEvent.LoadFavoriteMovies)
                                else -> { 
                                    if (currentSelectedTabType != null) {
                                        moviesViewModel.onEvent(MoviesEvent.SearchByGenre(currentSelectedTabType))
                                    } else { 
                                        moviesViewModel.onEvent(MoviesEvent.LoadPopularMovies)
                                    }
                                }
                            }
                        }
                    )
                }
                state.movies.isEmpty() -> { 
                    Column( modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(imageVector = Icons.Outlined.Movie, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = if (searchQuery.isNotBlank()) "No movies found for your search." else "No movies found in \"${state.currentListTitle}\".", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                    }
                }
                else -> { 
                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.movies.filter { movie -> selectedType == "All" || movie.type.equals(selectedType, ignoreCase = true) }) { movie ->
                            MovieListRow(movie = movie, onItemClick = {navController.navigate("movie_detail_screen/${movie.imdbId}")})
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(onDismissRequest = { showFilterDialog = false }, title = { Text("Filter by Type") }, text = { Column { listOf("All", "movie", "series", "episode").forEach { type -> Row(Modifier.fillMaxWidth().clickable { selectedType = type; showFilterDialog = false }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = selectedType == type, onClick = { selectedType = type; showFilterDialog = false }); Spacer(Modifier.width(8.dp)); Text(type.replaceFirstChar { it.uppercase() }) } } } }, confirmButton = { TextButton(onClick = { showFilterDialog = false }) { Text("Close") } })
    }
}
