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
) {

    fun getPagingMoviesCached(): Flow<PagingData<MovieItem>> {
        return remoteDataSource.getPagingMoviesDb()
    }

    suspend fun getMovieById(id: Int): Flow<Result<MovieByIdResponse>?> {
        return flow {
            val result = remoteDataSource.fetchMovieById(id)
            emit(result)
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    suspend fun getFavoriteMovies(): StateFlow<List<MovieItem>> {
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
            }
        }
    }

    suspend fun deleteFavoriteMovie(movie: MovieItem) {
        withContext(Dispatchers.IO) {
            appDatabase.runInTransaction {
                appDatabase.movieCacheDao().updateMovieById(false, movie.filmId)
                appDatabase.favoriteMoviesDao()
                    .removeFromFavorites(movie.filmId)
            }
        }
    }

}