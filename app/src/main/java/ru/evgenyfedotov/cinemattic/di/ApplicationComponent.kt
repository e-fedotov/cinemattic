package ru.evgenyfedotov.cinemattic.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import retrofit2.Retrofit
import ru.evgenyfedotov.cinemattic.data.local.AppDatabase
import ru.evgenyfedotov.cinemattic.data.local.FavoriteMoviesDao
import ru.evgenyfedotov.cinemattic.data.local.MovieCacheDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DatabaseModule::class
    ]
)
interface ApplicationComponent {

    fun inject(app: Application)
    fun provideRetrofit(): Retrofit
    fun provideMovieCacheDao(): MovieCacheDao
    fun provideFavoriteMovieDao(): FavoriteMoviesDao
    fun provideAppDatabase(): AppDatabase

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

}