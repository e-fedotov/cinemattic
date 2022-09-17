package ru.evgenyfedotov.cinemattic.viewmodel

import android.graphics.Movie
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.data.MovieRepository
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI
import ru.evgenyfedotov.cinemattic.model.MovieSearchResponse
import ru.evgenyfedotov.cinemattic.model.MovieTopResponse
import ru.evgenyfedotov.cinemattic.model.Result
import javax.inject.Inject

class MainViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private var mFavoriteMovies = MutableLiveData<List<MovieItem>>()
    val favoriteMovies: LiveData<List<MovieItem>>
        get() = mFavoriteMovies

    private val mToastStateFlow = MutableStateFlow(ToastStates.NO_TOAST)
    val toastStateFlow = mToastStateFlow.asStateFlow()

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

    enum class ToastStates {
        ADD_FAVORITES, REMOVE_FAVORITES, NO_TOAST
    }
}