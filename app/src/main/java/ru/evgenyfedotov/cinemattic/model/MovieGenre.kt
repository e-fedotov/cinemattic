package ru.evgenyfedotov.cinemattic.model

import com.google.gson.annotations.SerializedName

class MovieGenre(
    @SerializedName("genre") val genre: String
)
