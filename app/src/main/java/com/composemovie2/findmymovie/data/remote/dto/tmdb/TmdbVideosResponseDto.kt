package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbVideosResponseDto(
    // TMDB movie details endpoint with append_to_response=videos nests this under "videos"
    // So, if TmdbMovieDto has a field `videos: TmdbVideosResponseDto?`, this works.
    @SerializedName("results")
    val results: List<TmdbVideoDto>?
)
