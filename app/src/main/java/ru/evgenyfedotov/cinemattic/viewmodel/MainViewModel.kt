package ru.evgenyfedotov.cinemattic.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.data.MovieRepository
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.network.api.MovieDatabaseAPI
import ru.evgenyfedotov.cinemattic.model.MovieSearchResponse
import ru.evgenyfedotov.cinemattic.model.MovieTopResponse
import ru.evgenyfedotov.cinemattic.model.Result
import javax.inject.Inject

class MainViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val mMovieSearchResponse = MutableLiveData<ru.evgenyfedotov.cinemattic.model.Result<List<MovieItem>>>()
    val movieSearchResponse: LiveData<Result<List<MovieItem>>>
        get() = mMovieSearchResponse

    private val mFavoriteMovies = MutableLiveData<List<MovieItem>>()
    val favoriteMovies: LiveData<List<MovieItem>>
        get() = mFavoriteMovies

    var favoriteMoviesList = mutableListOf<MovieItem>()

    init {
        getFavoriteMoviesList()
    }

    fun getFavoriteMoviesList() {
        viewModelScope.launch {
            movieRepository.getFavoriteMovies().collect() { list ->
                mFavoriteMovies.value = list
            }
        }
    }


    fun fetchTopMovies(page: Int) {

        viewModelScope.launch {
            movieRepository.getTopMovies(page).collect() {
                if (it != null) {
                    mMovieSearchResponse.value = ru.evgenyfedotov.cinemattic.model.Result(it.status,
                        it.data?.films, it.error, it.message)
                }
            }
        }
    }

    fun removeFromFavorites(movie: MovieItem) {
        viewModelScope.launch {
            movieRepository.deleteFavoriteMovie(movie)
            getFavoriteMoviesList()
        }
    }

    fun addtoFavorites(movie: MovieItem) {
        viewModelScope.launch {
            movieRepository.addFavoriteMovie(movie)
            getFavoriteMoviesList()
        }
    }

}