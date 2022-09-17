package ru.evgenyfedotov.cinemattic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.data.MovieRepository
import ru.evgenyfedotov.cinemattic.model.MovieItem

class DetailsViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val mMovieItem = MutableLiveData<MovieItem>()
    val movieItem: LiveData<MovieItem>
        get() = mMovieItem

    fun getMovieById(id: Int) {
        viewModelScope.launch {
            movieRepository.getMovieById(id).collect() {
                if (it != null) {
                    mMovieItem.value = it.data?.data
                }
            }
        }
    }

}