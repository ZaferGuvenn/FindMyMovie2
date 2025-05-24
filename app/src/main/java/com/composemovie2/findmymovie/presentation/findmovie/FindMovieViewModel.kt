package com.composemovie2.findmymovie.presentation.findmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.composemovie2.findmymovie.data.remote.GeminiAiService
import com.composemovie2.findmymovie.domain.model.Movie // For TMDB search result
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendedMovieItem(
    val title: String,
    val reason: String,
    var tmdbId: Int? = null, // Changed from String to Int to match Movie.id
    var posterUrl: String? = null,
    var year: String? = null // Added year from TMDB
)

data class FindMovieScreenUiState(
    val isLoading: Boolean = false,
    val recommendations: List<RecommendedMovieItem> = emptyList(),
    val error: String? = null,
    val inputText: String = ""
)

@HiltViewModel
class FindMovieViewModel @Inject constructor(
    private val geminiAiService: GeminiAiService,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FindMovieScreenUiState())
    val uiState: StateFlow<FindMovieScreenUiState> = _uiState.asStateFlow()

    fun updateInputText(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun getRecommendations(userQuery: String) {
        if (userQuery.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a description for movie recommendations.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, recommendations = emptyList()) }

            val prompt = """
                You are a movie recommendation expert. Based on the following user description, please suggest up to 3 movie titles that are available on The Movie Database (TMDB).
                For each movie, provide a brief 1-2 sentence explanation for why it's a good match.
                User's description: '$userQuery'
                Please strictly follow this format for each movie:
                Movie Title: [Movie Title Here]
                Reason: [Your Explanation Here]
                Separate each movie entry with a double newline.
            """.trimIndent()

            try {
                val aiResponseText = geminiAiService.getMovieRecommendations(prompt)
                val parsedRecommendations = parseAiResponse(aiResponseText)
                
                if (parsedRecommendations.isEmpty()) {
                     _uiState.update { it.copy(isLoading = false, error = "Could not parse any recommendations from AI response.") }
                    return@launch
                }

                val enhancedRecommendations = mutableListOf<RecommendedMovieItem>()
                for (rec in parsedRecommendations) {
                    // Enhance with TMDB data
                    when (val tmdbResult = movieRepository.searchMovies(query = rec.title, page = 1)) {
                        is NetworkResult.Success -> {
                            val foundMovie: Movie? = tmdbResult.data?.firstOrNull()
                            if (foundMovie != null) {
                                rec.tmdbId = foundMovie.id
                                rec.posterUrl = foundMovie.posterPath // Assuming posterPath is the full URL or needs prefix
                                rec.year = foundMovie.year 
                            } else {
                                // Movie not found on TMDB, keep original AI data
                            }
                            enhancedRecommendations.add(rec)
                        }
                        is NetworkResult.Error -> {
                            // Error searching TMDB, add without TMDB info, maybe log error
                            enhancedRecommendations.add(rec)
                            // Optionally add a note to the reason or a specific error field in RecommendedMovieItem
                            // For now, just add it as is.
                        }
                        is NetworkResult.Loading -> {
                            // This specific item is loading.
                            // We might not update the individual item state here if the overall isLoading is true.
                            // For now, we'll just let the overall loading indicator handle this.
                            // If needed, we could add a loading flag to RecommendedMovieItem.
                            // We still need to add the 'rec' to enhancedRecommendations or it will be lost.
                            enhancedRecommendations.add(rec) // Add the item, its TMDB details are still loading
                        }
                    }
                }
                _uiState.update { it.copy(isLoading = false, recommendations = enhancedRecommendations, error = null) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    private fun parseAiResponse(responseText: String): List<RecommendedMovieItem> {
        val recommendations = mutableListOf<RecommendedMovieItem>()
        // Split by double newline, but also consider single newlines between blocks if that's what Gemini does.
        // Also, be robust to leading/trailing newlines in the overall response or blocks.
        val moviesData = responseText.trim().split(Regex("\n\n+")).filter { it.isNotBlank() }


        for (movieBlock in moviesData) {
            val lines = movieBlock.lines().map { it.trim() }.filter { it.isNotEmpty() }
            var title: String? = null
            var reason: String? = null
            var currentKey = ""

            for (line in lines) {
                if (line.startsWith("Movie Title:")) {
                    title = line.substringAfter("Movie Title:").trim()
                    currentKey = "title"
                } else if (line.startsWith("Reason:")) {
                    reason = line.substringAfter("Reason:").trim()
                    currentKey = "reason"
                } else {
                    // Handle potential multi-line reasons or titles if the AI doesn't strictly follow single lines
                    if (currentKey == "title" && title != null) {
                        title += " $line" // Append to title if it's a continuation
                    } else if (currentKey == "reason" && reason != null) {
                        reason += " $line" // Append to reason
                    }
                }
            }

            if (!title.isNullOrBlank() && !reason.isNullOrBlank()) {
                recommendations.add(RecommendedMovieItem(title = title!!, reason = reason!!))
            }
        }
        return recommendations
    }
}
