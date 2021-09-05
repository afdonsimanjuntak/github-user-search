package io.afdon.favourite.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.afdon.core.extension.toast
import io.afdon.core.viewmodel.SavedStateViewModelFactory
import io.afdon.favourite.R
import io.afdon.favourite.databinding.FragmentFavouriteBinding
import io.afdon.favourite.navigation.FavouriteNavigation
import javax.inject.Inject

class FavouriteFragment @Inject constructor(
    private val factory: SavedStateViewModelFactory,
    private val favouriteNavigation: FavouriteNavigation
) : Fragment(R.layout.fragment_favourite) {

    private val viewModel by viewModels<FavouriteViewModel> {
        factory.create(this@FavouriteFragment)
    }
    private var binding: FragmentFavouriteBinding? = null
    private lateinit var adapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initAdapter()
        setBinding(view)
        observeViewModel()
        viewModel.getFavourites()
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Favourite Users"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initAdapter() {
        adapter = FavouriteAdapter(
            viewModel::deleteFavourite, favouriteNavigation::openDetail
        )
    }

    private fun setBinding(v: View) {
        binding = FragmentFavouriteBinding.bind(v).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            rvFavouriteUsers.adapter = adapter
        }
    }

    private fun observeViewModel() {
        viewModel.favouriteUsers.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message -> toast(message) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
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