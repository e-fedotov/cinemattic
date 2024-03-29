package ru.evgenyfedotov.cinemattic.data.local

import androidx.paging.PagingSource
import androidx.room.*
import ru.evgenyfedotov.cinemattic.model.MovieItem

@Dao
interface MovieCacheDao {

    @Query("SELECT * FROM movies_cache")
    fun getAllMovies(): List<MovieItem>?

    @Query("SELECT * FROM movies_cache")
    fun getMoviesPageFactory(): PagingSource<Int, MovieItem>

    @Query("SELECT * FROM movies_cache WHERE filmId = :filmId")
    fun getMovieById(filmId: Int): MovieItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<MovieItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: MovieItem)

    @Update
    fun updateAll(movies: List<MovieItem>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateMovie(movie: MovieItem)

    @Query("UPDATE movies_cache SET isFavorite = :isFavorite WHERE filmId = :filmId")
    fun updateMovieById(isFavorite: Boolean, filmId: Int)

    @Delete
    fun deleteAll(movies: List<MovieItem>)

    @Delete
    fun deleteMovie(movie: MovieItem)

    @Query("DELETE FROM movies_cache")
    fun clearAllMovies()

}