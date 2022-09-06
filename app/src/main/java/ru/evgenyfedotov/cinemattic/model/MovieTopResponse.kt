package ru.evgenyfedotov.cinemattic.model

import com.google.gson.annotations.SerializedName

data class MovieTopResponse(
    @SerializedName("pagesCount") val pagesCount: Int,
    @SerializedName("films") val films: List<MovieItem>,
    @SerializedName("message") val message: String?
)
