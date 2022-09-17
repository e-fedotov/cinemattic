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
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.evgenyfedotov.cinemattic.di.DaggerMainListFragmentComponent
import ru.evgenyfedotov.cinemattic.mainrecycler.*
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.model.Result
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
    private lateinit var scrollListener: EndlessRecyclerViewScrollListenerImpl
    lateinit var layoutManager: LinearLayoutManager


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

        viewModel.favoriteMovies.observe(viewLifecycleOwner) {
            favoritesList.clear()
            it.forEach {
                favoritesList.add(it)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collect {
                    loading.isVisible = it.source.append is LoadState.Loading
                    loading.isVisible = it.source.prepend is LoadState.Loading

                    if (it.source.append is LoadState.Error || it.source.prepend is LoadState.Error) {
                        Snackbar.make(view, "Could not get items, try again", Snackbar.LENGTH_LONG)
                            .setAction("Retry") {
                                adapter.retry()
                            }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getPagingMovies().distinctUntilChanged().collectLatest { data ->
                adapter.submitData(lifecycle, data)
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
        adapter = MovieListPagingAdapter(object : MovieItemListener {
            override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
                if (isFavorite) {

                    // Отсюда надо убрать бизнес логику во вьмодель с помощью стейт флоу
                    // оттуда сделать колбек сюда и здесь уже решать что произошло
                    viewModel.addtoFavorites(item)

                    Snackbar.make(
                        view.findViewById(R.id.constraintLayout),
                        getString(R.string.snackbar_favorites_added),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.undo)) {
                            viewModel.removeFromFavorites(item)
                            recyclerView.adapter?.notifyItemChanged(position)
                        }
                        .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
                        .show()

                } else {
                    viewModel.removeFromFavorites(item)
                    Snackbar.make(
                        view.findViewById(R.id.constraintLayout),
                        getString(R.string.snackbar_favorites_removed),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.undo)) {
                            viewModel.addtoFavorites(item)
                            recyclerView.adapter?.notifyItemChanged(position)
                        }
                        .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
                        .show()
                }
            }

        })

        layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(view.context, 2)
            else -> LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

//        scrollListener = EndlessRecyclerViewScrollListenerImpl(layoutManager, viewModel)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

//        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
    }

    companion object {
        var favoritesList = mutableListOf<MovieItem>()
    }

}