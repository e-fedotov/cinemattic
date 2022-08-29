package ru.evgenyfedotov.cinemattic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.data.MovieRepository
import ru.evgenyfedotov.cinemattic.model.MovieItem

class FavoritesViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val mFavoriteMovies = MutableLiveData<List<MovieItem>>()
    val favoriteMovies: LiveData<List<MovieItem>>
        get() = mFavoriteMovies

    init {
        getFavoriteMoviesList()
    }

    fun getFavoriteMoviesList() {
        viewModelScope.launch {
            movieRepository.getFavoriteMovies().collect() {
                mFavoriteMovies.value = it
            }
        }
    }

    fun removeFromFavorites(movie: MovieItem) {
        viewModelScope.launch {
            movieRepository.deleteFavoriteMovie(movie)
        }
    }

    fun addtoFavorites(movie: MovieItem) {
        viewModelScope.launch {
            movieRepository.addFavoriteMovie(movie)
        }
    }

}