package ru.evgenyfedotov.cinemattic.data.remote

import android.graphics.Movie
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import ru.evgenyfedotov.cinemattic.data.PagingRemoteMediator
import ru.evgenyfedotov.cinemattic.data.local.AppDatabase
import ru.evgenyfedotov.cinemattic.model.*
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI
import java.io.IOException
import javax.inject.Inject

class MoviesRemoteDataSource @Inject constructor(
    private val retrofit: Retrofit,
    private val appDatabase: AppDatabase
) {

    private val apiService = retrofit.create(MovieDatabaseAPI::class.java)

    fun getTopMoviesPagingFlow(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<MovieItem>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { GetTopMoviesPagingSource(retrofit) }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPagingMoviesDb(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<MovieItem>> {
        val pagingSourceFactory = { appDatabase.movieCacheDao().getMoviesPageFactory() }
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = PagingRemoteMediator(retrofit, appDatabase)
        ).flow
    }

    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 10, initialLoadSize = 30, enablePlaceholders = true)
    }


    suspend fun fetchTopMovies(page: Int): Result<MovieSearchResponse> {
        return getResponse(
            request = { apiService.getTopMovies("TOP_250_BEST_FILMS", page) },
            defaultErrorMessage = "Could not get Top movies from server"
        )
    }

    suspend fun fetchMovieById(id: Int): Result<MovieByIdResponse> {
        return getResponse(
            request = { apiService.getMovie(id) },
            defaultErrorMessage = "Could not get Movie"
        )
    }

    suspend fun searchMovieByKeyword(keyword: String, page: Int): Result<MovieSearchResponse> {
        return getResponse(
            request = { apiService.searchMovieByKeyword(keyword, page) },
            defaultErrorMessage = "Movie Search Failed"
        )
    }

    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): Result<T> {

        return try {
            val result = request.invoke()
            if (result.isSuccessful) {
                return Result.success(result.body())
            } else {
                val errorResponse = parseErrorMessage(result, retrofit)
                Result.error(errorResponse?.status_message ?: defaultErrorMessage, errorResponse)
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            Result.error("Unknown error", null)
        }

    }

    private fun parseErrorMessage(
        response: Response<*>,
        retrofit: Retrofit
    ): ru.evgenyfedotov.cinemattic.model.Error? {
        val converter = retrofit.responseBodyConverter<ru.evgenyfedotov.cinemattic.model.Error>(
            ru.evgenyfedotov.cinemattic.model.Error::class.java,
            arrayOfNulls(0)
        )
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            Error()
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

}