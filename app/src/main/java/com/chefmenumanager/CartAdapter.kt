package com.chefmenumanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chefmenumanager.databinding.ItemCartBinding

class CartAdapter(
    private val onIncrease: (MenuItem) -> Unit,
    private val onDecrease: (MenuItem) -> Unit,
    private val onRemove: (MenuItem) -> Unit
) : ListAdapter<CartLine, CartAdapter.CartViewHolder>(CartDiffCallback) {

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(line: CartLine) {
            val item = line.item
            binding.cartDishName.text = item.dishName
            binding.cartUnitPrice.text = binding.root.context.getString(R.string.price_each, item.price.toRand())
            binding.cartQuantity.text = line.quantity.toString()
            binding.cartSubtotal.text = line.subtotalCents.centsToRand()

            binding.increaseButton.setOnClickListener { onIncrease(item) }
            binding.decreaseButton.setOnClickListener { onDecrease(item) }
            binding.removeButton.setOnClickListener { onRemove(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object CartDiffCallback : DiffUtil.ItemCallback<CartLine>() {
    override fun areItemsTheSame(oldItem: CartLine, newItem: CartLine): Boolean = oldItem.item.id == newItem.item.id
    override fun areContentsTheSame(oldItem: CartLine, newItem: CartLine): Boolean = oldItem == newItem
}
