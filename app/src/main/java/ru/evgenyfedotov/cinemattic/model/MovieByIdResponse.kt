package ru.evgenyfedotov.cinemattic.model

import com.google.gson.annotations.SerializedName

data class MovieByIdResponse(
    @SerializedName("data") val data: MovieItem
)
