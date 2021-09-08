package io.afdon.search.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.afdon.core.databinding.ItemRecyclerviewLoadMoreBinding
import io.afdon.core.databinding.ItemRecyclerviewLoadingBinding
import io.afdon.core.viewholder.LoadMoreViewHolder
import io.afdon.core.viewholder.LoadingViewHolder
import io.afdon.search.R
import io.afdon.search.databinding.ItemSearchResultBinding
import io.afdon.search.model.User

class SearchResultAdapter(
    private val toggleFavourite: (Item) -> Unit,
    private val openDetail: (String) -> Unit,
    private val loadMore: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.user?.id == newItem.user?.id && oldItem.type == newItem.type

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.isFavourite == newItem.isFavourite
    })

    override fun getItemViewType(position: Int): Int {
        return listDiffer.currentList[position].type
    }

    fun submitList(list: List<Item>?, commitCallback: Runnable? = null) {
        listDiffer.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_search_result -> {
                Holder(
                    ItemSearchResultBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    toggleFavourite,
                    openDetail
                )
            }
            R.layout.item_recyclerview_loading -> {
                LoadingViewHolder(
                    ItemRecyclerviewLoadingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.item_recyclerview_load_more -> {
                LoadMoreViewHolder(
                    ItemRecyclerviewLoadMoreBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ), loadMore
                )
            }
            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("---------------------", "onBindViewHolder: index: $position")
        when (holder) {
            is Holder -> holder.bind(listDiffer.currentList[position])
            is LoadMoreViewHolder -> holder.bind()
        }
    }

    class Holder(
        private val binding: ItemSearchResultBinding,
        private val toggleFavourite: (Item) -> Unit,
        private val openDetail: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.item = item
            binding.ibFavourite.setOnClickListener { toggleFavourite(item) }
            binding.bOpenDetail.setOnClickListener { item.user?.login?.let { openDetail(it) } }
        }
    }

    data class Item(
        val user: User? = null,
        var isFavourite: Boolean = false,
        val type: Int = R.layout.item_search_result
    )

    class SearchContent(
        private val items: List<Item>,
        isNewSearch : Boolean = false
    ) {

        var isNewSearch = isNewSearch
            private set

        fun getItems() : List<Item>{
            this.isNewSearch = false
            return items
        }
    }
}