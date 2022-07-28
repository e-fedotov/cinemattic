package ru.evgenyfedotov.cinemattic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.evgenyfedotov.cinemattic.data.MovieRepository
import javax.inject.Inject

class FavoritesViewModelFactory  @Inject constructor(private val movieRepository: MovieRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(movieRepository) as T
    }
}

