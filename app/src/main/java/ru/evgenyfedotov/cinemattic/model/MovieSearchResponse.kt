package ru.evgenyfedotov.cinemattic.model

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("keyword") val keyword: String?,
    @SerializedName("pagesCount") val pagesCount: Int,
    @SerializedName("searchFilmsCountResult") val searchFilmsCountResult: Int?,
    @SerializedName("films") val films: List<MovieItem>
)
