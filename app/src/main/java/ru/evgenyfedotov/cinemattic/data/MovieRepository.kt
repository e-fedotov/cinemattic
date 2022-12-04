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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.evgenyfedotov.cinemattic.data.local.AppDatabase
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
    private val appDatabase: AppDatabase,
    private val getTopMoviesPagingSource: GetTopMoviesPagingSource
) {

//    fun getPagingMovies(): Flow<PagingData<MovieItem>> {
//        return remoteDataSource.getTopMoviesPagingFlow()
//    }

    fun getPagingMoviesCached(): Flow<PagingData<MovieItem>> {
        return remoteDataSource.getPagingMoviesDb()
    }

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

    suspend fun getMovieById(id: Int): Flow<Result<MovieByIdResponse>?> {
        return flow {
            val result = remoteDataSource.fetchMovieById(id)
            emit(result)
        }
            .distinctUntilChanged()
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

    suspend fun getFavoriteMovies(): StateFlow<List<MovieItem>> {

        // Здесь надо попробовать перейти на Flow прямо из базы
        // и при получении нового значения флоу обновлять избранное автоматически и отсылать в VM
        var favoriteMoviesIds: List<FavoriteMovieItem>?
        val favoriteMoviesItems = mutableListOf<MovieItem>()
        withContext(Dispatchers.IO) {
            favoriteMoviesIds = favoriteMoviesDao.getAllFavorites()
            favoriteMoviesIds?.forEach { item ->
                movieCacheDao.getMovieById(item.filmId)?.let {
                    favoriteMoviesItems.add(it)
                }
            }
        }

        return MutableStateFlow(favoriteMoviesItems)
    }

    suspend fun addFavoriteMovie(movie: MovieItem) {
        withContext(Dispatchers.IO) {

            appDatabase.runInTransaction {
                appDatabase.movieCacheDao().updateMovieById(true, movie.filmId)
                appDatabase.favoriteMoviesDao()
                    .addFavoriteItem(FavoriteMovieItem(filmId = movie.filmId))
//                movie.isFavorite = true
//                appDatabase.movieCacheDao().updateMovie(movie)
            }
        }
//        withContext(Dispatchers.IO) {
//            favoriteMoviesDao.addFavoriteItem(FavoriteMovieItem(filmId = movie.filmId))
//            movie.isFavorite = true
//            movieCacheDao.updateMovie(movie)
//        }
//        Thread(Runnable {
//
//        }).start()
    }

    suspend fun deleteFavoriteMovie(movie: MovieItem) {
        withContext(Dispatchers.IO) {
            appDatabase.runInTransaction {
                appDatabase.movieCacheDao().updateMovieById(false, movie.filmId)
                appDatabase.favoriteMoviesDao()
                    .removeFromFavorites(movie.filmId)
//                movie.isFavorite = true
//                appDatabase.movieCacheDao().updateMovie(movie)
            }
        }
//        Thread(Runnable {
//            favoriteMoviesDao.removeFromFavorites(movie.filmId)
//        }).start()

    }

}