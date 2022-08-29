package ru.evgenyfedotov.cinemattic.di

import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI
import javax.inject.Scope


@Module
object MainListFragmentModule {

    @Provides
    fun providesApiService(retrofit: Retrofit): MovieDatabaseAPI {
        return retrofit.create(MovieDatabaseAPI::class.java)
    }

}

