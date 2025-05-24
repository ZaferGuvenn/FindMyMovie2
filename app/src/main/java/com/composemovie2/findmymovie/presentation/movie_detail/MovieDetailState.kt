package com.composemovie2.findmymovie.presentation.movie_detail

import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.model.WatchProviderGroup 

data class MovieDetailState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null,

    val watchProviderGroups: List<WatchProviderGroup>? = null,
    val isLoadingWatchProviders: Boolean = false,
    val watchProvidersError: String? = null,

    val subscribedProviderIds: Set<String> = emptySet() // New
)
