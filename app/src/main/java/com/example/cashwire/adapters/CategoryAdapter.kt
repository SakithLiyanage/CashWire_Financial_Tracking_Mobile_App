package com.example.cashwire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cashwire.databinding.ItemCategoryGridBinding
import com.example.cashwire.models.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(private val binding: ItemCategoryGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name
            binding.ivCategory.setImageResource(category.iconRes)
            binding.cardCategoryIcon.setCardBackgroundColor(category.colorHex and 0x33FFFFFF)
            binding.ivCategory.setColorFilter(category.colorHex)

            binding.root.setOnClickListener {
                onCategorySelected(category)
            }
        }
    }
}