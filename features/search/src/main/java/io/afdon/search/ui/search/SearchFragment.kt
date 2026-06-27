package io.afdon.search.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.afdon.core.extension.toast
import io.afdon.core.viewmodel.SavedStateViewModelFactory
import io.afdon.search.R
import io.afdon.search.navigation.SearchNavigation
import javax.inject.Inject

class SearchFragment @Inject constructor(
    private val factory: SavedStateViewModelFactory,
    private val searchNavigation: SearchNavigation
) : Fragment() {

    private val viewModel by viewModels<SearchViewModel> {
        factory.create(this@SearchFragment, arguments)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onItemClick = { searchNavigation.openDetail(it) },
                        onFavouriteClick = { viewModel.toggleFavorite(it) },
                        onRefresh = { viewModel.newSearch() },
                        onScrollLoadMore = { viewModel.onScrollLoadMore() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        observeViewModel()
        viewModel.updateItems()
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Github User Search"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun observeViewModel() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message -> toast(message) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.page_search, menu)
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
}