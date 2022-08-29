package ru.evgenyfedotov.cinemattic.model

import com.google.gson.annotations.SerializedName

data class MovieTopResponse(
    @SerializedName("pagesCount") val pagesCount: Int,
    @SerializedName("films") val films: List<MovieItem>
)
