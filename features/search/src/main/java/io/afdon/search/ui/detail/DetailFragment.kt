package io.afdon.search.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.afdon.core.viewmodel.SavedStateViewModelFactory
import io.afdon.search.R
import io.afdon.search.databinding.FragmentDetailBinding
import javax.inject.Inject

class DetailFragment @Inject constructor(
    private val factory: SavedStateViewModelFactory
): Fragment(R.layout.fragment_detail) {

    private val viewModel by viewModels<DetailViewModel> {
        factory.create(this@DetailFragment, arguments)
    }
    private var binding: FragmentDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailBinding.bind(view).apply {
            vm = viewModel
        }
        viewModel.isFavourite.observe(viewLifecycleOwner) {
            val res = if (it) R.drawable.ic_star_yellow else R.drawable.ic_star_outline
            binding?.ivFavourite?.setImageResource(res)
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "User Detail"
            setDisplayHomeAsUpEnabled(true)
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