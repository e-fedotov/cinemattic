package ru.evgenyfedotov.cinemattic.data.remote

import android.graphics.Movie
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.create
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.model.MovieSearchResponse
import ru.evgenyfedotov.cinemattic.model.Result
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI
import java.io.IOException
import java.lang.Integer.max
import javax.inject.Inject

const val DEFAULT_PAGE = 1

class GetTopMoviesPagingSource @Inject constructor(private val retrofit: Retrofit) : PagingSource<Int, MovieItem>() {

    private fun ensureValidKey(key: Int) = max(DEFAULT_PAGE, key)

    override fun getRefreshKey(state: PagingState<Int, MovieItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val item = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = item.dbId - (state.config.pageSize / 2))
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieItem> {
        val page = params.key ?: DEFAULT_PAGE
        val api = retrofit.create(MovieDatabaseAPI::class.java)
        return try {
            val response = api.getTopMovies("TOP_250_BEST_FILMS", page)

            LoadResult.Page(
                response.body()!!.films, prevKey = if (page == DEFAULT_PAGE) null else page - 1,
                nextKey = if (response.body()!!.films.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

}