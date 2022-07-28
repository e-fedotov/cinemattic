package ru.evgenyfedotov.cinemattic.mainrecycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModel

class EndlessRecyclerViewScrollListenerImpl(layoutManager: LinearLayoutManager, private val viewModel: MainViewModel) : EndlessRecyclerViewScrollListener(layoutManager) {
    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        viewModel.fetchTopMovies(page)
    }
}