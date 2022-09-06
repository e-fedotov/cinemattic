package ru.evgenyfedotov.cinemattic

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.di.DaggerMainListFragmentComponent
import ru.evgenyfedotov.cinemattic.mainrecycler.*
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModel
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModelFactory
import javax.inject.Inject

class MainListFragment : Fragment() {

    private var movieItems = listOf<MovieItem>()

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { mainViewModelFactory }
    private lateinit var adapter: MovieListPagingAdapter

    //    private lateinit var adapter: MovieListAdapter
    private lateinit var recyclerView: RecyclerView
    private val list = ArrayList<MovieItem>()
    private lateinit var layoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_list, container, false)

        DaggerMainListFragmentComponent.builder().applicationComponent(App.getAppInstance()).build()
            .inject(this)
        initRecycler(view)

//        viewModel.fetchTopMovies(1)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loading = view.findViewById<ProgressBar>(R.id.loading)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
//
//        viewModel.favoriteMovies.observe(viewLifecycleOwner) {
//            favoritesList.clear()
//            it.forEach {
//                favoritesList.add(it)
//            }
//        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.Loading) {
                loading.isVisible = true
            } else {
                loading.isVisible = false
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append  is LoadState.Error -> loadState.append  as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                   Snackbar.make(view, it.error.message ?: "Could not get items", Snackbar.LENGTH_LONG).setAction("Retry") {
                       adapter.retry()
                   }
                       .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
                       .show()
                }
            }

        }
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                adapter.loadStateFlow.collect {
//                    loading.isVisible = it.source.append is LoadState.Loading
//                    loading.isVisible = it.source.prepend is LoadState.Loading
//
//                    if (it.source.append is LoadState.Error || it.source.prepend is LoadState.Error) {
//                        Snackbar.make(view, "Could not get items, try again", Snackbar.LENGTH_LONG)
//                            .setAction("Retry") {
//                                adapter.retry()
//                            }
//                    }
//                }
//            }
//        }

        lifecycleScope.launch {
            viewModel.getPagingMovies().asLiveData().observe(viewLifecycleOwner) { data ->
                adapter.submitData(lifecycle, data)
            }
//            viewModel.getPagingMovies().distinctUntilChanged().collectLatest { data ->
//                adapter.submitData(lifecycle, data)
//            }
        }

        lifecycleScope.launch {
            viewModel.toastStateFlow.collect {
                makeSnackbar(requireView(), it)
            }
        }

//        viewModel.movieSearchResponse.observe(viewLifecycleOwner) { response ->
//            when (response.status) {
//                Result.Status.SUCCESS -> {
//                    response.data?.let {
//                        adapter.updateItems(it)
//                    }
//                    loading.visibility = View.GONE
//                }
//
//                Result.Status.ERROR -> {
//                    response.message?.let {
//                        Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
//                    }
//                    loading.visibility = View.GONE
//                }
//
//                Result.Status.LOADING -> {
//                    loading.visibility = View.VISIBLE
//                }
//            }
//        }

    }

    private fun initRecycler(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)

        // В адаптер надо добавлять лайвдату с избранными фильмами из вьюмодели
        adapter = MovieListPagingAdapter(listener = object : MovieItemListener {
            override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
                viewModel.onFavoriteClickManager(item, isFavorite, position)
//                if (!isFavorite) {
//
//                    // Отсюда надо убрать бизнес логику во вьмодель с помощью стейт флоу
//                    // оттуда сделать колбек сюда и здесь уже решать что произошло
//                    viewModel.addToFavorites(item)
//
//                    Snackbar.make(
//                        view.findViewById(R.id.constraintLayout),
//                        getString(R.string.snackbar_favorites_added),
//                        Snackbar.LENGTH_LONG
//                    )
//                        .setAction(getString(R.string.undo)) {
//                            viewModel.removeFromFavorites(item)
//                            adapter.retry()
////                            recyclerView.adapter?.notifyItemChanged(position)
//
//                        }
//                        .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
//                        .show()
//
//                } else {
//                    viewModel.removeFromFavorites(item)
//                    Snackbar.make(
//                        view.findViewById(R.id.constraintLayout),
//                        getString(R.string.snackbar_favorites_removed),
//                        Snackbar.LENGTH_LONG
//                    )
//                        .setAction(getString(R.string.undo)) {
//                            viewModel.addToFavorites(item)
////                            recyclerView.adapter?.notifyItemChanged(position)
//                            adapter.notifyItemChanged(position)
//                        }
//                        .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
//                        .show()
//                }
            }

        }, lifecycleOwner = viewLifecycleOwner, favoriteMoviesList = viewModel.favoriteMovies)

        layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(view.context, 2)
            else -> LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
    }

    private fun makeSnackbar(view: View, toastStates: MainViewModel.ToastStates) {
        when (toastStates) {
            MainViewModel.ToastStates.ADD_FAVORITES ->
                Snackbar.make(
                    view.findViewById(R.id.constraintLayout),
                    getString(R.string.snackbar_favorites_added),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo)) {
                        viewModel.removeFromFavorites(viewModel.currentActionableMovieItem!!)
                        adapter.snapshot().get(viewModel.currentPosition)?.isFavorite = false
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                    .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
                    .show()

            MainViewModel.ToastStates.REMOVE_FAVORITES ->
                Snackbar.make(
                    view.findViewById(R.id.constraintLayout),
                    getString(R.string.snackbar_favorites_removed),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo)) {
                        viewModel.addToFavorites(viewModel.currentActionableMovieItem!!)
                        adapter.snapshot().get(viewModel.currentPosition)?.isFavorite = true
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                    .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
                    .show()

            MainViewModel.ToastStates.NO_TOAST -> return
        }
    }

    companion object {
        var favoritesList = mutableListOf<MovieItem>()
    }

}