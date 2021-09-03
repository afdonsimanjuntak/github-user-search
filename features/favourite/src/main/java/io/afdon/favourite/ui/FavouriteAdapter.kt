package io.afdon.favourite.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.afdon.favourite.databinding.ItemFavouriteBinding
import io.afdon.favourite.model.User

class FavouriteAdapter(
    private val delete: (Item) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.Holder>() {

    private val listDiffer = AsyncListDiffer<Item>(this, object : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.user.id == newItem.user.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    })

    fun submitList(list: List<Item>) = listDiffer.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, delete)
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: Holder, position: Int) =
        holder.bind(listDiffer.currentList[position])

    class Holder(
        private val binding: ItemFavouriteBinding,
        private val delete: (Item) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.item = item
            binding.ibDelete.setOnClickListener { delete(item) }
        }
    }

    data class Item(
        val user: User
    )
}