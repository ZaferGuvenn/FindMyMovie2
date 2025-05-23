package com.composemovie2.findmymovie.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbCreditsDto(
    // TMDB movie details endpoint with append_to_response=credits nests this under "credits"
    @SerializedName("cast")
    val cast: List<TmdbCastDto>?,
    @SerializedName("crew")
    val crew: List<TmdbCrewDto>?
)
