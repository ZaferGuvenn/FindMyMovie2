package com.composemovie2.findmymovie.presentation

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composemovie2.findmymovie.presentation.theme.FindMyMovie2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindMyMovie2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier.fillMaxWidth().background(Color.Cyan)) {

            Card (
                border = BorderStroke(2.dp, Color.Black),
                modifier=modifier.width(200.dp).height(200.dp)
                ) {
                AsyncImage(
                    modifier=modifier.fillMaxSize(),
                    model = R.drawable.star_on,
                    contentDescription = "bilmem"
                )
            }

            Text(
                text = "Deneme composu",
                modifier = modifier.padding(2.dp).background(Color.Cyan)
            )

            Text(
                text = "Hello $name!",
                modifier = modifier
            )
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FindMyMovie2Theme {
        Greeting("Android")
    }
}