package ru.evgenyfedotov.cinemattic.model

import com.google.gson.annotations.SerializedName

data class MovieCountry (
    @SerializedName("country")
    val country: String
)
