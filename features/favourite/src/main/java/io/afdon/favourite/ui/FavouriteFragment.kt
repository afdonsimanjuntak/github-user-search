package io.afdon.favourite.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.afdon.core.viewmodel.SavedStateViewModelFactory
import io.afdon.favourite.R
import io.afdon.favourite.databinding.FragmentFavouriteBinding
import javax.inject.Inject

class FavouriteFragment @Inject constructor(
    private val factory: SavedStateViewModelFactory,
) : Fragment(R.layout.fragment_favourite) {

    private val viewModel by viewModels<FavouriteViewModel> {
        factory.create(this@FavouriteFragment)
    }
    private var binding: FragmentFavouriteBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Favourite Users"
            setDisplayHomeAsUpEnabled(true)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view).apply {
            vm = viewModel
        }
        val adapter = FavouriteAdapter(viewModel::deleteFavourite)
        binding?.rvFavouriteUsers?.adapter = adapter
        viewModel.favouriteUsers.observe(viewLifecycleOwner) {
            adapter.submitList(it)
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