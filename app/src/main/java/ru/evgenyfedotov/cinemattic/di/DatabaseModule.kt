package ru.evgenyfedotov.cinemattic.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import ru.evgenyfedotov.cinemattic.data.local.AppDatabase
import ru.evgenyfedotov.cinemattic.data.local.FavoriteMoviesDao
import ru.evgenyfedotov.cinemattic.data.local.MovieCacheDao
import ru.evgenyfedotov.cinemattic.data.local.PagingKeysDao
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "movies_cache.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMovieDao(appDatabase: AppDatabase): MovieCacheDao {
        return appDatabase.movieCacheDao()
    }

    @Provides
    fun provideFavoriteMovieDao(appDatabase: AppDatabase): FavoriteMoviesDao {
        return appDatabase.favoriteMoviesDao()
    }

    @Provides
    fun providePagingKeysDao(appDatabase: AppDatabase): PagingKeysDao {
        return appDatabase.pagingKeysDao()
    }

}