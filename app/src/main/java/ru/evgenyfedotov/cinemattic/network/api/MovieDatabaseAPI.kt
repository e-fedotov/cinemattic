package ru.evgenyfedotov.cinemattic.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.evgenyfedotov.cinemattic.model.MovieSearchResponse
import ru.evgenyfedotov.cinemattic.model.MovieTopResponse

interface MovieDatabaseAPI {

    @GET("search-by-keyword")
    suspend fun searchMovieByKeyword(
        @Query("keyword") keyword: String,
        @Query("page") page: Int
    ): Response<MovieSearchResponse>

    @GET("top")
    suspend fun getTopMovies(
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<MovieSearchResponse>

}
