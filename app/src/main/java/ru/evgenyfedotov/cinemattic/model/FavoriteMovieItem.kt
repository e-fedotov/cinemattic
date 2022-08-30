package ru.evgenyfedotov.cinemattic.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["filmId"], unique = true)
    ]
)
data class FavoriteMovieItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val filmId: Int
)
