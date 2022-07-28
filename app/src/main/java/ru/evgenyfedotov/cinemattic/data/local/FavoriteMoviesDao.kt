package ru.evgenyfedotov.cinemattic.data.local

import androidx.room.*
import ru.evgenyfedotov.cinemattic.model.FavoriteMovieItem

@Dao
interface FavoriteMoviesDao {

    @Query("DELETE FROM FavoriteMovieItem WHERE filmId = :filmId")
    fun removeFromFavorites(filmId: Int)

    @Query("SELECT * FROM FavoriteMovieItem")
    fun getAllFavorites(): List<FavoriteMovieItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteItem(favorite: FavoriteMovieItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllFavoriteItems(favorites: List<FavoriteMovieItem>)

}
