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

    override fun onCreate() {
        super.onCreate()
        INSTANCE = DaggerApplicationComponent.factory().create(this)
        DaggerApplicationComponent.factory().create(this).inject(this)
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