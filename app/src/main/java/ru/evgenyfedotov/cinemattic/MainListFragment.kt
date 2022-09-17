package ru.evgenyfedotov.cinemattic

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { mainViewModelFactory }

    private val adapter = MovieListPagingAdapter(listener = object : MovieItemListener {
        override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
            viewModel.onFavoriteClickManager(item, isFavorite, position)
        }
    })

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerMainListFragmentComponent.builder().applicationComponent(App.getAppInstance()).build()
            .inject(this)

        lifecycleScope.launch {
            viewModel.getPagingMovies().collectLatest { data ->
                Log.d("Create", "onCreateView: create")
                adapter.submitData(lifecycle, data)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_list, container, false)
        initRecycler(view)
        Log.d("MainList", "onCreateView: HERE!")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = view.findViewById(R.id.loading)

        createLoadStateListener()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastStateFlow.collect {
                makeSnackbar(requireView(), it)
            }
        }

    }

    private fun createLoadStateListener() {
        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                loading.isVisible = true
            } else {
                loading.isVisible = false
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }

                error?.let {
                    Snackbar.make(
                        requireView(),
                        it.error.message ?: getString(R.string.default_network_error_msg),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.retry)) {
                            adapter.retry()
                        }
                        .setAnchorView(view?.rootView?.findViewById(R.id.bottomNavigation))
                        .show()
                }
            }
        }
    }

    private fun initRecycler(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)

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
                    }
                    .setAnchorView(view.rootView.findViewById(R.id.bottomNavigation))
                    .show()

            MainViewModel.ToastStates.NO_TOAST -> return
        }
    }

}