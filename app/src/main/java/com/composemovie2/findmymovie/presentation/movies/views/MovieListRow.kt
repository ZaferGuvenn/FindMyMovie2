package com.composemovie2.findmymovie.presentation.movies.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composemovie2.findmymovie.domain.model.Movie

@Composable
fun MovieListRow(
    movie: Movie,
    onItemClick: (Movie) -> Unit
) {

    Row(
        modifier = Modifier.padding(12.dp).clickable{onItemClick(movie)}.width(150.dp)
    ) {


        Column() {
            Card() {

                Card() {
                    Image(
                        modifier = Modifier.size(150.dp,200.dp).background(Color.Black).clip(
                            CircleShape
                        ),
                        contentDescription = movie.title,
                        painter = rememberAsyncImagePainter(
                            //"https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Thomas_Edison2-crop.jpg/640px-Thomas_Edison2-crop.jpg"
                            movie.poster

                        )

                    )

                    Text(text = movie.title, modifier = Modifier.padding(14.dp), fontSize = 24.sp)
                    Text(text = movie.year, modifier = Modifier.padding(14.dp), fontSize = 22.sp)
                    Text(text = movie.type, modifier = Modifier.padding(14.dp), fontSize = 20.sp)


                }



            }
        }



//        Image(
//            modifier = Modifier.size(200.dp,200.dp).background(Color.Black),
//            contentDescription = movie.title,
//            painter = rememberAsyncImagePainter(
//                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Thomas_Edison2-crop.jpg/640px-Thomas_Edison2-crop.jpg"
//            )
//
//        )
//
//        AsyncImage(
//            model = movie.poster,
//            contentDescription = movie.title,
//            modifier = Modifier
//                .size(200.dp)
//                .background(Color.Gray),
//            onError = {
//                Log.e("ImageLoad", "Image load failed: ${it.result.throwable}")
//            }
//        )
//
//        val context = LocalContext.current
//
//        Image(
//            painter = rememberAsyncImagePainter(
//                ImageRequest.Builder(context)
//                    .data("https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Thomas_Edison2-crop.jpg/640px-Thomas_Edison2-crop.jpg")
//                    .crossfade(true)
//                    .build()
//            ),
//            contentDescription = movie.title,
//            modifier = Modifier
//                .size(200.dp)
//                .background(Color.Gray)
//        )


    }

}