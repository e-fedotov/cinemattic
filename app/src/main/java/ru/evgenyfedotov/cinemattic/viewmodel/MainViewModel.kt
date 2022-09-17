package ru.evgenyfedotov.cinemattic.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.MainActivity
import ru.evgenyfedotov.cinemattic.data.MovieRepository
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.workers.AlarmNotificationReceiver
import java.util.*

class MainViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private var mFavoriteMovies = MutableLiveData<List<MovieItem>>()
    val favoriteMovies: LiveData<List<MovieItem>>
        get() = mFavoriteMovies

    private val mToastStateFlow = MutableStateFlow(ToastStates.NO_TOAST)
    val toastStateFlow = mToastStateFlow.asStateFlow()

    private val mMovieItem = MutableLiveData<MovieItem>()
    val movieItem: LiveData<MovieItem>
        get() = mMovieItem

    var currentActionableMovieItem: MovieItem? = null
    var currentPosition = 0

    init {
        getFavoriteMoviesList()
    }

    fun getPagingMovies(): Flow<PagingData<MovieItem>> {
        return movieRepository.getPagingMoviesCached().cachedIn(viewModelScope)
    }

    fun getFavoriteMoviesList() {
        viewModelScope.launch {
            movieRepository.getFavoriteMovies().collect { list ->
                mFavoriteMovies.value = list
            }
        }
    }

    fun getMovieById(id: Int) {
        viewModelScope.launch {
            movieRepository.getMovieById(id).collect {
                if (it != null) {
                    mMovieItem.value = it.data?.data
                }
            }
        }
    }

    fun isMovieFavorite(movie: MovieItem): Boolean {
        return mFavoriteMovies.value?.find { movieItem -> movieItem.filmId == movie.filmId } != null
    }

     fun removeFromFavorites(movie: MovieItem) {
        viewModelScope.launch {
            movieRepository.deleteFavoriteMovie(movie)
            mToastStateFlow.emit(ToastStates.REMOVE_FAVORITES)
            mToastStateFlow.emit(ToastStates.NO_TOAST)
            getFavoriteMoviesList()
        }
    }

    fun addToFavorites(movie: MovieItem) {
        viewModelScope.launch {
            movieRepository.addFavoriteMovie(movie)
            mToastStateFlow.emit(ToastStates.ADD_FAVORITES)
            mToastStateFlow.emit(ToastStates.NO_TOAST)
            getFavoriteMoviesList()
        }
    }

    fun onFavoriteClickManager(movie: MovieItem, isFavorite: Boolean, position: Int) {
        currentActionableMovieItem = movie
        currentPosition = position
        if (isFavorite) {
            removeFromFavorites(movie)
        } else {
            addToFavorites(movie)
        }
    }

    fun scheduleAlarm(
        context: Context,
        date: Calendar,
        movieTitle: String?,
        movieDescription: String?,
        movieId: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java)

        alarmIntent.putExtra(MainActivity.MOVIE_ID, movieId)
        alarmIntent.putExtra(MainActivity.TITLE_KEY, movieTitle)
        alarmIntent.putExtra(MainActivity.DESCRIPTION_KEY, movieDescription)

        val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            movieId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            date.timeInMillis,
            alarmPendingIntent
        )
    }

    enum class ToastStates {
        ADD_FAVORITES, REMOVE_FAVORITES, NO_TOAST
    }
}