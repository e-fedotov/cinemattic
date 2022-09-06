package ru.evgenyfedotov.cinemattic.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "movies_cache",
    indices = [
        Index(value = ["filmId"], unique = true)
    ]
)

data class MovieItem(
//    @PrimaryKey
    @SerializedName("filmId")
    val filmId: Int,

    @PrimaryKey(autoGenerate = true)
    val dbId: Int = 0,

    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("type") val type: MovieType?,
    @SerializedName("year") val year: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("filmLength") val filmLength: String?,
    @SerializedName("countries") val countries: List<MovieCountry>,
    @SerializedName("genres") val genres: List<MovieGenre>,
    @SerializedName("rating") val rating: String?,
    @SerializedName("ratingVoteCount") val ratingVoteCount: Int?,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("posterUrlPreview") val posterUrlPreview: String,

    var page: Int?,
    var isFavorite: Boolean = false
)
