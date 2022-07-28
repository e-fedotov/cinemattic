package ru.evgenyfedotov.cinemattic.di

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.evgenyfedotov.cinemattic.BuildConfig



@Module
object NetworkModule {

    private const val API_KEY = "0a341361-8c29-4595-8946-3fb8c25861e6"
    private const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/v2.1/films/"

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain
                    .request().run {
                        newBuilder()
                            .addHeader("X-API-KEY", API_KEY)
                            .build()
                    })
            }
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    })
            .build()
    }

}