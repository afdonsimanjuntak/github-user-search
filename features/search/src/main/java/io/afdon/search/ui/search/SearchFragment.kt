package io.afdon.search.ui.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.afdon.core.extension.toast
import io.afdon.core.viewmodel.SavedStateViewModelFactory
import io.afdon.search.R
import io.afdon.search.databinding.FragmentSearchBinding
import io.afdon.search.navigation.SearchNavigation
import javax.inject.Inject

class SearchFragment @Inject constructor(
    private val factory: SavedStateViewModelFactory,
    private val searchNavigation: SearchNavigation
) : Fragment(R.layout.fragment_search) {

    private val viewModel by viewModels<SearchViewModel> {
        factory.create(this@SearchFragment, arguments)
    }
    private var binding: FragmentSearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Github User Search"
            setDisplayHomeAsUpEnabled(false)
        }
        binding = FragmentSearchBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
        val adapter = SearchResultAdapter(
            viewModel::toggleFavorite, searchNavigation::openDetail
        )
        binding?.rvSearchResult?.adapter = adapter
        viewModel.searchResultItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            with (binding?.swipeRefreshLayout) {
                if (it) {
                    if (this?.isRefreshing == false) isRefreshing = true
                } else {
                    this?.isRefreshing = false
                }
            }
        }
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel.searchUser()
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message -> toast(message) }
        }
        viewModel.getFavouriteUserIds()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favourite -> {
                searchNavigation.showFavourites()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}