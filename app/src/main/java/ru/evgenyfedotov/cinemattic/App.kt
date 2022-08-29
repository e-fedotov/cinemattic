package ru.evgenyfedotov.cinemattic

import android.app.Application
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.evgenyfedotov.cinemattic.di.ApplicationComponent
import ru.evgenyfedotov.cinemattic.di.DaggerApplicationComponent
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI

private const val API_KEY = "0a341361-8c29-4595-8946-3fb8c25861e6"
private const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/v2.1/films/"

class App : Application() {

    lateinit var api: MovieDatabaseAPI
    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        INSTANCE = DaggerApplicationComponent.factory().create(this)
        DaggerApplicationComponent.factory().create(this).inject(this)
    }

    private fun initRetrofit(): Retrofit {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createHttpClient())
            .build()

        api = retrofit.create(MovieDatabaseAPI::class.java)
        return retrofit

    }

    private fun createHttpClient(): OkHttpClient {

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

    companion object {
        lateinit var instance: App
            private set

        private var INSTANCE: ApplicationComponent? = null
        fun getAppInstance(): ApplicationComponent {
            return INSTANCE!!
        }
    }

}