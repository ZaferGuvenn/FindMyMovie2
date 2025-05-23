package com.composemovie2.findmymovie.domain.mapper

import com.composemovie2.findmymovie.data.remote.dto.tmdb.TmdbGenreDto
import com.composemovie2.findmymovie.domain.model.Genre
import javax.inject.Inject

class TmdbGenreMapper @Inject constructor() : Mapper<TmdbGenreDto, Genre> {
    override fun map(from: TmdbGenreDto): Genre {
        return Genre(
            id = from.id ?: 0, // Default to 0 if null, though ID should ideally always be present
            name = from.name ?: "Unknown Genre" // Default name if null
        )
    }
}
