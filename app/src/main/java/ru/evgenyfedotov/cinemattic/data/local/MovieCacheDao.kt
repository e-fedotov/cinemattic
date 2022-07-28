package ru.evgenyfedotov.cinemattic.data.local

import androidx.room.*
import ru.evgenyfedotov.cinemattic.model.MovieItem

@Dao
interface MovieCacheDao {

    @Query("SELECT * FROM movies_cache")
    fun getAllMovies(): List<MovieItem>?

    @Query("SELECT * FROM movies_cache WHERE isFavorite = 'true'")
    fun getFavoriteMovies(): List<MovieItem>?

    @Query("SELECT * FROM movies_cache WHERE filmId = :filmId")
    fun getMovieById(filmId: Int): MovieItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<MovieItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: MovieItem)

    @Update
    fun updateAll(movies: List<MovieItem>)

    @Update
    fun updateMovie(movie: MovieItem)

    @Delete
    fun deleteAll(movies: List<MovieItem>)

    @Delete
    fun deleteMovie(movie: MovieItem)

}