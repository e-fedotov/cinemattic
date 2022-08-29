package ru.evgenyfedotov.cinemattic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PagingKeys(
    @PrimaryKey val keyId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
