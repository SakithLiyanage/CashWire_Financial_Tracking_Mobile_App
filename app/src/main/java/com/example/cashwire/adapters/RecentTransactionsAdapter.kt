package com.example.cashwire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashwire.R
import com.example.cashwire.databinding.ItemRecentTransactionBinding
import com.example.cashwire.models.Categories
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.TransactionType
import com.example.cashwire.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class RecentTransactionsAdapter :
    ListAdapter<Transaction, RecentTransactionsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRecentTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTransactionTitle.text = transaction.title

            // Format and set date
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            binding.tvTransactionDate.text = dateFormat.format(Date(transaction.date))

            // Format amount based on transaction type
            val amountText = if (transaction.type == TransactionType.EXPENSE) {
                "-${CurrencyFormatter.formatLKR(transaction.amount)}"
            } else {
                "+${CurrencyFormatter.formatLKR(transaction.amount)}"
            }
            binding.tvTransactionAmount.text = amountText

            // Set amount text color based on transaction type
            val amountColor = if (transaction.type == TransactionType.EXPENSE) {
                ContextCompat.getColor(binding.root.context, R.color.negative_red)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.positive_green)
            }
            binding.tvTransactionAmount.setTextColor(amountColor)

            // Set category icon and color
            val category = findCategory(transaction.category)
            if (category != null) {
                binding.ivCategoryIcon.setImageResource(category.iconRes)
                binding.cardCategoryIcon.setCardBackgroundColor(category.colorHex and 0x33FFFFFF)
                binding.ivCategoryIcon.setColorFilter(category.colorHex)
            } else {
                // Default icon and color if category not found
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_other)
                binding.cardCategoryIcon.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.accent_light)
                )
                binding.ivCategoryIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.accent)
                )
            }
        }

        private fun findCategory(categoryId: String) =
            Categories.expenseCategories.find { it.id == categoryId || it.name == categoryId } ?:
            Categories.incomeCategories.find { it.id == categoryId || it.name == categoryId }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}