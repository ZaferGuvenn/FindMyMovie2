package com.composemovie2.findmymovie.data.remote.dto

import com.google.gson.annotations.SerializedName


data class Rating(
    @SerializedName("Source")
    val source: String,
    @SerializedName("Value")
    val value: String
)