package io.afdon.search.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.afdon.search.databinding.ItemSearchResultBinding
import io.afdon.search.model.User
import javax.inject.Inject

class SearchResultAdapter(
    private val toggleFavourite: (Item) -> Unit,
    private val openDetail: (String) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.Holder>() {

    private val listDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.user.id == newItem.user.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.isFavourite == newItem.isFavourite
    })

    fun submitList(list: List<Item>) = listDiffer.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            toggleFavourite,
            openDetail
        )

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: Holder, position: Int) =
        holder.bind(listDiffer.currentList[position])

    class Holder(
        private val binding: ItemSearchResultBinding,
        private val toggleFavourite: (Item) -> Unit,
        private val openDetail: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.item = item
            binding.ibFavourite.setOnClickListener { toggleFavourite(item) }
            binding.bOpenDetail.setOnClickListener { openDetail(item.user.login) }
        }
    }

    data class Item(
        val user: User,
        var isFavourite: Boolean
    )
}