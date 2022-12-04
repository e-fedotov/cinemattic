package ru.evgenyfedotov.cinemattic.data

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.evgenyfedotov.cinemattic.data.local.AppDatabase
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.model.PagingKeys
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val DEFAULT_PAGE = 1

@OptIn(ExperimentalPagingApi::class)
class PagingRemoteMediator @Inject constructor(
    private val retrofit: Retrofit,
    private val appDatabase: AppDatabase
): RemoteMediator<Int, MovieItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieItem>
    ): MediatorResult {

        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> {
                    Log.d("paging", "load: $state")
                    val remoteKeys = getClosestRemoteKey(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    Log.d("paging", "load: $state")
                    val remoteKeys = getFirstRemoteKey(state)
                    remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = false)
                }
                LoadType.APPEND -> {
                    Log.d("paging", "load: $state")
                    val lastItem = state.lastItemOrNull()
                    val remoteKey: PagingKeys? = appDatabase.withTransaction {
                        if (lastItem?.filmId != null) {
                            appDatabase.pagingKeysDao().getPagingKeyId(lastItem.filmId)
                        } else null
                    }

                    if (remoteKey?.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            }

            val api = retrofit.create(MovieDatabaseAPI::class.java)
            val response = api.getTopMovies("TOP_250_BEST_FILMS", loadKey ?: 1)

            if (response.code() == 402) {
                val jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
                val error = jsonObject.get("message")
                Log.d("HALLO", "load: $error")
                MediatorResult.Error(Throwable(error.toString()))
            } else {
                val resBody = response.body()
                val movies = resBody?.films
                val isEndOfList = movies?.isEmpty()
                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH && response.code() != 402) {
                        appDatabase.movieCacheDao().clearAllMovies()
                        appDatabase.pagingKeysDao().clearAllPagingKeys()
                    }
                    val prevKey = if (loadKey == DEFAULT_PAGE) null else loadKey - 1
                    val nextKey = if (isEndOfList == true) null else loadKey + 1
                    val favorites = appDatabase.favoriteMoviesDao().getAllFavorites()
                    movies?.forEach {
                        it.page = loadKey
                        appDatabase.pagingKeysDao()
                            .insertKey(PagingKeys(it.filmId, prevKey, nextKey))

                        it.isFavorite = favorites?.find { fav -> fav.filmId == it.filmId }?.filmId == it.filmId
                        appDatabase.movieCacheDao().insertMovie(it)
                    }
                }

                MediatorResult.Success(endOfPaginationReached = isEndOfList == true)
            }

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, MovieItem>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
//                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
//                    ?: throw InvalidObjectException("Invalid state, key should not be null")
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, MovieItem>): PagingKeys? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { movie -> appDatabase.pagingKeysDao().getPagingKeyId(movie.filmId) }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, MovieItem>): PagingKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { movie -> appDatabase.pagingKeysDao().getPagingKeyId(movie.filmId) }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, MovieItem>): PagingKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.filmId?.let { filmId ->
                appDatabase.pagingKeysDao().getPagingKeyId(filmId)
            }
        }
    }

}