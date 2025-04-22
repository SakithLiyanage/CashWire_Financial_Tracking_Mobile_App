package com.example.cashwire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashwire.R
import com.example.cashwire.data.BudgetRepository
import com.example.cashwire.databinding.ItemCategoryBudgetBinding
import com.example.cashwire.models.CategoryBudget
import com.example.cashwire.utils.CurrencyFormatter

class CategoryBudgetAdapter(
    private val onItemClick: (CategoryBudget) -> Unit
) : ListAdapter<CategoryBudget, CategoryBudgetAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBudgetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCategoryBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val budgetRepository = BudgetRepository.getInstance(binding.root.context)

        fun bind(item: CategoryBudget) {
            // Set category icon and name
            binding.ivCategoryIcon.setImageResource(item.categoryIconRes)
            binding.cardCategoryIcon.setCardBackgroundColor(item.categoryColor and 0x33FFFFFF)
            binding.ivCategoryIcon.setColorFilter(item.categoryColor)
            binding.tvCategoryName.text = item.categoryName

            // Get category spending - FIXED to pass the whole CategoryBudget object
            val spent = budgetRepository.getCategorySpending(item)

            // Format amounts
            binding.tvSpentAmount.text = CurrencyFormatter.formatLKR(spent)
            binding.tvBudgetAmount.text = CurrencyFormatter.formatLKR(item.amount)

            // Calculate and set usage percentage
            val percentage = budgetRepository.getCategoryBudgetUsage(item)
            binding.tvPercentage.text = "$percentage%"
            binding.progressCategoryBudget.progress = percentage

            // Update UI based on budget usage
            val progressColor = when {
                percentage >= 90 -> R.color.negative_red
                percentage >= 75 -> R.color.warning_orange
                else -> R.color.accent
            }
            binding.progressCategoryBudget.setIndicatorColor(
                binding.root.context.getColor(progressColor)
            )

            // Set click listener
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CategoryBudget>() {
        override fun areItemsTheSame(oldItem: CategoryBudget, newItem: CategoryBudget): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryBudget, newItem: CategoryBudget): Boolean {
            return oldItem == newItem
        }
    }
}