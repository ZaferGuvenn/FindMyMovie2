package com.composemovie2.findmymovie.presentation.movies.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MoviesSearchBar(

    onSearch: (String)->Unit

) {

    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {



        TextField(
            value = searchText,
            onValueChange = { searchText = it},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = {Text("Batman")},
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions (
                onDone = {
                    onSearch(searchText)
                    keyboardController?.hide()
                }
            )
        )

    }

}


@Preview(showBackground = true)
@Composable
fun PreviewSearchBar() {
    MaterialTheme {
        MoviesSearchBar { query ->
            println("Aranan kelime: $query")
        }
    }
}