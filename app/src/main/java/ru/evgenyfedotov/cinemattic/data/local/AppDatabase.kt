package ru.evgenyfedotov.cinemattic.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.evgenyfedotov.cinemattic.model.FavoriteMovieItem
import ru.evgenyfedotov.cinemattic.model.MovieItem

@TypeConverters(GenreCountryTypeConverters::class)
@Database(
    entities = [
        MovieItem::class,
        FavoriteMovieItem::class
    ], version = 6
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieCacheDao(): MovieCacheDao
    abstract fun favoriteMoviesDao(): FavoriteMoviesDao

}