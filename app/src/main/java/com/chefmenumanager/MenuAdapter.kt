package com.chefmenumanager

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chefmenumanager.databinding.ItemMenuBinding

class MenuAdapter(
    private val onAddToCart: (MenuItem) -> Unit,
    private val onEdit: (MenuItem) -> Unit,
    private val onDelete: (MenuItem) -> Unit
) : ListAdapter<MenuItem, MenuAdapter.MenuViewHolder>(MenuDiffCallback) {

    inner class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.dishName.text = item.dishName
            binding.dishPrice.text = item.price.toRand()
            binding.dishDescription.text = item.description
            binding.courseBadge.text = item.course

            val courseColor = ContextCompat.getColor(binding.root.context, Courses.colorFor(item.course))
            val badgeBackground = GradientDrawable()
            badgeBackground.cornerRadius = 100f * binding.root.resources.displayMetrics.density
            badgeBackground.setColor(courseColor)
            binding.courseBadge.background = badgeBackground
            binding.courseAccent.setBackgroundColor(courseColor)

            binding.addToCartButton.setOnClickListener { onAddToCart(item) }
            binding.editButton.setOnClickListener { onEdit(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object MenuDiffCallback : DiffUtil.ItemCallback<MenuItem>() {
    override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean = oldItem == newItem
}
