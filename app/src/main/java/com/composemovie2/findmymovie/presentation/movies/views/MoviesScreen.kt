package com.composemovie2.findmymovie.presentation.movies.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composemovie2.findmymovie.presentation.movies.MoviesViewModel

@Composable
fun MoviesScreen(
    navController: NavController,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {

    val state = moviesViewModel.state.value

    Box(modifier= Modifier.fillMaxSize()){

    }

}