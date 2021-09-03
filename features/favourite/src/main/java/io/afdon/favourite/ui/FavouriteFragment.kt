package io.afdon.favourite.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Favourite Users"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentFavouriteBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
        val adapter = FavouriteAdapter(
            viewModel::deleteFavourite, favouriteNavigation::openDetail
        )
        binding?.rvFavouriteUsers?.adapter = adapter
        viewModel.favouriteUsers.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message -> toast(message) }
        }
        viewModel.getFavourites()
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