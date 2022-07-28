package ru.evgenyfedotov.cinemattic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteMovieItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val filmId: Int
)
