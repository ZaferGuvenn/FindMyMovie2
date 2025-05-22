package com.composemovie2.findmymovie.presentation.movies.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.composemovie2.findmymovie.presentation.movies.MoviesEvent
import com.composemovie2.findmymovie.presentation.movies.MoviesViewModel

@Composable
fun MoviesScreen(
    navController: NavController,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {

    val state = moviesViewModel.state.value

    Box(modifier= Modifier.fillMaxSize()){

        Column() {

            //movie search bar
            MoviesSearchBar {
                moviesViewModel.onEvent(MoviesEvent.Search(it))
            }

            //lazy column
            print(state.movies)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.movies) { movie->
                    println("movie")
                    println(movie)
                    MovieListRow(movie, onItemClick = {
                       //navigate to detail
                        navController.navigate("")
                    })
                }
            }

        }
    }

    if (state.errorMsg.isNotBlank()){

        Box(
            modifier= Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = state.errorMsg, color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(14.dp)
            )
        }


    }

    if (state.isLoading){

        Box(
            modifier= Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){

            CircularProgressIndicator()
        }


    }



}

