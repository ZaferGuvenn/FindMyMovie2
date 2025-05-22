package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.MoviesDto
import com.composemovie2.findmymovie.domain.model.Movie
import javax.inject.Inject

class MoviesDTOMapper @Inject constructor(): Mapper<MoviesDto, List<Movie>> {
    override fun map(from: MoviesDto): List<Movie> {
        return from.search.map {
            Movie(
                poster = it.poster,
                title = it.title,
                imdbId = it.imdbID,
                type = it.type,
                year = it.year
            )
        }
    }
}