package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.MovieDetailDto
import com.composemovie2.findmymovie.domain.model.MovieDetail
import javax.inject.Inject

class MovieDetailDTOMapper @Inject constructor(): Mapper<MovieDetailDto, MovieDetail> {
    override fun map(from: MovieDetailDto): MovieDetail {
        return MovieDetail(
            title = from.title,
            year = from.year,
            poster = from.poster,
            released = from.released,
            imdbRating = from.imdbRating,
            language = from.language,
            overview = from.plot,
            actors = from.actors,
            awards = from.awards,
            country = from.country,
            director = from.director,
            backdropPath = null,
            runtime = from.runtime?.substringBefore(" min")?.toIntOrNull(),
            tagline = null,
            status = null,
            voteCount = from.imdbVotes?.replace(",", "")?.toIntOrNull(),
            genresList = from.genre?.split(", ") ?: emptyList(),
            cast = emptyList(),
            crew = emptyList(),
            videos = emptyList()
        )
    }
}