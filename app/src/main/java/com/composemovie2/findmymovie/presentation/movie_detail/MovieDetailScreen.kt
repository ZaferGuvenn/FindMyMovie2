package com.composemovie2.findmymovie.presentation.movie_detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke // New
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Star // New
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp 
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.composemovie2.findmymovie.domain.model.CastMember
import com.composemovie2.findmymovie.domain.model.CrewMember
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.model.Video
import com.composemovie2.findmymovie.domain.model.WatchProvider 
import com.composemovie2.findmymovie.domain.model.WatchProviderGroup 
import androidx.compose.material.icons.filled.Person 
import androidx.compose.material.icons.filled.Favorite 
import androidx.compose.material.icons.filled.FavoriteBorder 
import androidx.compose.runtime.collectAsState 

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val isFavorite by viewModel.isFavorite.collectAsState() 

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        state.movieDetail?.title ?: "Movie Detail", 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { 
                    IconButton(onClick = { viewModel.toggleFavoriteStatus() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary 
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary 
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
                state.isLoading && state.movieDetail == null -> { 
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMsg != null && state.movieDetail == null -> { 
                    com.composemovie2.findmymovie.presentation.components.CommonErrorView( 
                        message = state.errorMsg ?: "An unknown error occurred.",
                        onRetry = { viewModel.retryFetchMovieDetail() }
                    )
                }
                state.movieDetail != null -> {
                    MovieDetailContent(movie = state.movieDetail!!, state = state) 
                }
                else -> { 
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
fun MovieDetailContent(movie: MovieDetail, state: MovieDetailState) { 
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (!movie.backdropPath.isNullOrBlank()) {
            AsyncImage(model = movie.backdropPath, contentDescription = "${movie.title} backdrop", modifier = Modifier.fillMaxWidth().height(220.dp), contentScale = ContentScale.Crop, error = { Icon(Icons.Outlined.BrokenImage, "Error loading backdrop") })
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(MaterialTheme.colorScheme.surfaceVariant)) { Icon(Icons.Outlined.Movie, "No backdrop", modifier = Modifier.align(Alignment.Center).size(100.dp)) }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            if (!movie.tagline.isNullOrBlank()) { Text(text = movie.tagline!!, style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary); Spacer(modifier = Modifier.height(8.dp)) }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { Text("Year: ${movie.year}", style = MaterialTheme.typography.bodyMedium); movie.runtime?.let { Text("Runtime: ${it}min", style = MaterialTheme.typography.bodyMedium) }; movie.status?.let { Text("Status: $it", style = MaterialTheme.typography.bodyMedium) } }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { Text("Rating: ${movie.imdbRating}/10", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold); movie.voteCount?.let { Text("(${it} votes)", style = MaterialTheme.typography.labelSmall) } }
            Spacer(modifier = Modifier.height(8.dp))
            if (movie.genresList.isNotEmpty()) { Text("Genres: ${movie.genresList.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium); Spacer(modifier = Modifier.height(16.dp)) }
            Text("Overview", style = MaterialTheme.typography.titleLarge); Text(movie.overview, style = MaterialTheme.typography.bodyLarge); Spacer(modifier = Modifier.height(16.dp))

            if (movie.videos.isNotEmpty()) { Text("Trailers", style = MaterialTheme.typography.titleLarge); LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) { items(movie.videos.filter { it.site == "YouTube" && (it.type == "Trailer" || it.type == "Teaser") }) { video -> Card(modifier = Modifier.width(180.dp).clickable { val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${video.key}")); val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=${video.key}")); try { context.startActivity(appIntent) } catch (ex: Exception) { context.startActivity(webIntent) } }, shape = RoundedCornerShape(8.dp)) { Box { AsyncImage(model = video.thumbnailUrl, contentDescription = video.name, modifier = Modifier.height(100.dp).fillMaxWidth(), contentScale = ContentScale.Crop, error = { Icon(Icons.Outlined.BrokenImage, "Error") }); Icon(Icons.Filled.PlayCircleOutline, contentDescription = "Play trailer", tint = Color.White, modifier = Modifier.align(Alignment.Center).size(48.dp)) }; Text(video.name, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(8.dp)) } } }; Spacer(modifier = Modifier.height(16.dp)) }
            if (movie.cast.isNotEmpty()) { Text("Cast", style = MaterialTheme.typography.titleLarge); LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) { items(movie.cast.take(10)) { member -> CastCrewItem(name = member.name, roleOrJob = member.character, profilePath = member.profilePath) } }; Spacer(modifier = Modifier.height(16.dp)) }
            val keyCrew = movie.crew.filter { it.job == "Director" || it.job == "Screenplay" || it.job == "Writer" || it.department == "Production" }.distinctBy { it.id }.take(10)
            if (keyCrew.isNotEmpty()) { Text("Key Crew", style = MaterialTheme.typography.titleLarge); LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) { items(keyCrew) { member -> CastCrewItem(name = member.name, roleOrJob = member.job, profilePath = member.profilePath) } } }

            if (state.isLoadingWatchProviders) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)) } 
            else if (!state.watchProvidersError.isNullOrBlank()) { Text(text = "Watch Providers: ${state.watchProvidersError}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp)) } 
            else if (!state.watchProviderGroups.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp)); Text("Where to Watch", style = MaterialTheme.typography.titleLarge)
                state.watchProviderGroups!!.forEach { group ->
                    Spacer(modifier = Modifier.height(8.dp)); Text(group.type, style = MaterialTheme.typography.titleMedium)
                    group.tmdbLink?.let { link -> TextButton(onClick = { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)); context.startActivity(intent) }) { Text("View all options on TMDB for this region", fontSize = 12.sp) } }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(group.providers) { provider ->
                            ProviderChip(
                                provider = provider,
                                isSubscribed = state.subscribedProviderIds.contains(provider.providerId.toString()) // Updated
                            )
                        }
                    }
                }
            }
        } 
    } 
}

@Composable
fun CastCrewItem(name: String, roleOrJob: String, profilePath: String?) { 
    Card(modifier = Modifier.width(120.dp), shape = RoundedCornerShape(8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(model = profilePath, contentDescription = name, modifier = Modifier.height(150.dp).fillMaxWidth().clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)), contentScale = ContentScale.Crop, error = { Box(modifier = Modifier.height(150.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)) { Icon(Icons.Filled.Person, "No image", modifier = Modifier.align(Alignment.Center).size(50.dp)) } })
            Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            Text(roleOrJob, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 4.dp).padding(bottom = 4.dp))
        }
    }
}

@Composable
fun ProviderChip(provider: WatchProvider, isSubscribed: Boolean) { // Updated signature
    val cardBorder = if (isSubscribed) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.width(100.dp),
        border = cardBorder // Apply border
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box { // To overlay star
                AsyncImage(
                    model = provider.logoPath,
                    contentDescription = "${provider.providerName} logo",
                    modifier = Modifier.height(40.dp).width(40.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Fit,
                    error = { Icon(Icons.Outlined.BrokenImage, "Logo error", modifier = Modifier.size(24.dp)) }
                )
                if (isSubscribed) {
                    Icon(
                        imageVector = Icons.Filled.Star, 
                        contentDescription = "Subscribed",
                        tint = MaterialTheme.colorScheme.primary, // Or a different color like yellow
                        modifier = Modifier.align(Alignment.TopEnd).size(16.dp).padding(1.dp) // Adjusted padding
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = provider.providerName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
