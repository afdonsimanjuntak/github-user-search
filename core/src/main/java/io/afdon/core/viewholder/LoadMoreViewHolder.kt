package io.afdon.core.viewholder

import androidx.recyclerview.widget.RecyclerView
import io.afdon.core.databinding.ItemRecyclerviewLoadMoreBinding

class LoadMoreViewHolder(
    private val binding: ItemRecyclerviewLoadMoreBinding,
    private val loadMore: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.bLoadMore.setOnClickListener { loadMore() }
    }
}