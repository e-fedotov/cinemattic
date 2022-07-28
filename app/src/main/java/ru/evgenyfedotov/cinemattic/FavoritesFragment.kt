package ru.evgenyfedotov.cinemattic

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.evgenyfedotov.cinemattic.di.DaggerFavoritesFragmentComponent
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieItemListener
import ru.evgenyfedotov.cinemattic.mainrecycler.MovieListAdapter
import ru.evgenyfedotov.cinemattic.model.MovieItem
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModel
import ru.evgenyfedotov.cinemattic.viewmodel.MainViewModelFactory
import javax.inject.Inject

class FavoritesFragment : Fragment() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { mainViewModelFactory }

    private lateinit var adapter: MovieListAdapter
    private lateinit var recyclerView: RecyclerView
    private val favoriteMovies = mutableListOf<MovieItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        DaggerFavoritesFragmentComponent.builder().applicationComponent(App.getAppInstance()).build().inject(this)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteMoviesList()
        initRecycler(view)

        viewModel.favoriteMovies.observe(viewLifecycleOwner) {
            adapter.updateItems(it)
            MainListFragment.favoritesList.clear()
            it.forEach {
                MainListFragment.favoritesList.add(it)
            }
        }
    }

    fun addToFavorites(movieItem: MovieItem) {
        viewModel.addtoFavorites(movieItem)
    }

    fun removeFromFavorites(movieItem: MovieItem) {
        viewModel.removeFromFavorites(movieItem)
    }

    private fun initRecycler(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        adapter = MovieListAdapter(favoriteMovies, object : MovieItemListener {
            override fun onFavoriteClick(item: MovieItem, isFavorite: Boolean, position: Int) {
                removeFromFavorites(item)
//                adapter.notifyItemRemoved(position)
            }
        })

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(view.context, 2)
            else -> LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_activity_favorites)
    }



}