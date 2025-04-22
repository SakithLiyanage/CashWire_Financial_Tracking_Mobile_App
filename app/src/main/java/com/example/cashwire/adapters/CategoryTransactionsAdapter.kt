package com.example.cashwire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashwire.databinding.ItemCategoryHeaderBinding
import com.example.cashwire.models.CategoryTransactions

class CategoryTransactionsAdapter(private val currencyCode: String = "LKR") :
    RecyclerView.Adapter<CategoryTransactionsAdapter.CategoryViewHolder>() {

    private var categories = listOf<CategoryTransactions>()

    fun updateData(newCategories: List<CategoryTransactions>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryHeaderBinding.inflate(
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

    inner class CategoryViewHolder(private val binding: ItemCategoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryTransactions: CategoryTransactions) {
            binding.tvCategoryName.text = categoryTransactions.category
            binding.tvTransactionCount.text =
                "${categoryTransactions.transactions.size} transaction(s)"

            // Set up nested recycler view for transactions
            val transactionsAdapter = TransactionAdapter(currencyCode)
            binding.recyclerTransactions.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = transactionsAdapter
                isNestedScrollingEnabled = false
            }

            // Update adapter data
            transactionsAdapter.updateData(categoryTransactions.transactions)
        }
    }
}