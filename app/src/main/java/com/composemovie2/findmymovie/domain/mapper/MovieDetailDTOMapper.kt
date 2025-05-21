package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto
import com.composemovie2.findmymovie.domain.model.MovieDetail
import javax.inject.Inject

class MovieDetailDTOMapper @Inject constructor(): Mapper<MovieDetailDto, MovieDetail> {
    override fun map(from: MovieDetailDto): MovieDetail {

        return MovieDetail(
            actors = from.actors,
            awards = from.awards,
            country = from.country,
            director = from.director,
            imdbRating = from.imdbRating,
            language = from.language,
            poster = from.poster,
            released = from.released,
            title = from.title,
            year = from.year
        )

    }
}