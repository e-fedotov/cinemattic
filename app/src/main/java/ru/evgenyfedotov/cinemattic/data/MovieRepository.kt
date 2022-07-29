package ru.evgenyfedotov.cinemattic.data

import android.graphics.Movie
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.evgenyfedotov.cinemattic.data.local.FavoriteMoviesDao
import ru.evgenyfedotov.cinemattic.data.local.MovieCacheDao
import ru.evgenyfedotov.cinemattic.data.remote.GetTopMoviesPagingSource
import ru.evgenyfedotov.cinemattic.data.remote.MoviesRemoteDataSource
import ru.evgenyfedotov.cinemattic.model.*
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieCacheDao: MovieCacheDao,
    private val favoriteMoviesDao: FavoriteMoviesDao,
    private val remoteDataSource: MoviesRemoteDataSource,
    private val getTopMoviesPagingSource: GetTopMoviesPagingSource
) {

    suspend fun getTopMovies(page: Int): Flow<Result<List<MovieItem>>?> {
        return flow {
            emit(getCachedMovies())
            emit(Result.loading())
            val result = remoteDataSource.fetchTopMovies(page)

            if (result.status == Result.Status.SUCCESS) {
                result.data?.films?.let {
                    movieCacheDao.deleteAll(it)
                    movieCacheDao.insertAll(it)
                }
            }

//            emit(result)
            val newResult = Result(result.status, result.data?.films, result.error, result.message)
            emit(newResult)

        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    private fun getCachedMovies(): Result<List<MovieItem>>? {
//        return movieCacheDao.getAllMovies()?.let {
//            Result.success(
//                MovieSearchResponse(
//                    films = it,
//                    keyword = "",
//                    pagesCount = -1,
//                    searchFilmsCountResult = -1
//                )
//            )
//        }
        return movieCacheDao.getAllMovies()?.let {
            Result.success(it)
        }
    }

    suspend fun getFavoriteMovies(): Flow<List<MovieItem>> {
        return flow {
            val favoriteMoviesIds = favoriteMoviesDao.getAllFavorites()
            val favoriteMoviesItems = mutableListOf<MovieItem>()

            favoriteMoviesIds?.forEach { item ->
                movieCacheDao.getMovieById(item.filmId)?.let {
                    favoriteMoviesItems.add(it)
                }
            }

            emit(favoriteMoviesItems)
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    suspend fun addFavoriteMovie(movie: MovieItem) {

        withContext(Dispatchers.IO) {
            favoriteMoviesDao.addFavoriteItem(FavoriteMovieItem(filmId = movie.filmId))
        }
//        Thread(Runnable {
//
//        }).start()
    }

    suspend fun deleteFavoriteMovie(movie: MovieItem) {
        withContext(Dispatchers.IO) {
            favoriteMoviesDao.removeFromFavorites(movie.filmId)
        }
//        Thread(Runnable {
//            favoriteMoviesDao.removeFromFavorites(movie.filmId)
//        }).start()

    }

}