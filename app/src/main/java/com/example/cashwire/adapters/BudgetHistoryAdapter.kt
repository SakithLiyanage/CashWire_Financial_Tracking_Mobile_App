package com.example.cashwire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashwire.R
import com.example.cashwire.databinding.ItemBudgetHistoryBinding
import com.example.cashwire.models.BudgetAction
import com.example.cashwire.models.BudgetHistory
import com.example.cashwire.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class BudgetHistoryAdapter :
    ListAdapter<BudgetHistory, BudgetHistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBudgetHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemBudgetHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetHistory) {
            // Format date
            val dateFormat = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())
            val date = dateFormat.format(Date(item.timestamp))
            binding.tvBudgetChangeDate.text = date

            // Set title and amount based on action
            when (item.action) {
                BudgetAction.SET_MAIN_BUDGET -> {
                    binding.tvBudgetChangeTitle.text = "Monthly budget set"
                    binding.tvBudgetChangeAmount.text = CurrencyFormatter.formatLKR(item.newAmount)
                    binding.tvBudgetChangeAmount.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                    )
                }

                BudgetAction.INCREASE_MAIN_BUDGET -> {
                    binding.tvBudgetChangeTitle.text = "Monthly budget increased"
                    val difference = item.newAmount - (item.previousAmount ?: 0.0)
                    binding.tvBudgetChangeAmount.text = "+${CurrencyFormatter.formatLKR(difference)}"
                    binding.tvBudgetChangeAmount.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.positive_green)
                    )
                }

                BudgetAction.DECREASE_MAIN_BUDGET -> {
                    binding.tvBudgetChangeTitle.text = "Monthly budget decreased"
                    val difference = (item.previousAmount ?: 0.0) - item.newAmount
                    binding.tvBudgetChangeAmount.text = "-${CurrencyFormatter.formatLKR(difference)}"
                    binding.tvBudgetChangeAmount.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.negative_red)
                    )
                }

                BudgetAction.SET_CATEGORY_BUDGET -> {
                    binding.tvBudgetChangeTitle.text = "${item.categoryName} budget set"
                    binding.tvBudgetChangeAmount.text = CurrencyFormatter.formatLKR(item.newAmount)
                    binding.tvBudgetChangeAmount.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                    )
                }

                BudgetAction.UPDATE_CATEGORY_BUDGET -> {
                    binding.tvBudgetChangeTitle.text = "${item.categoryName} budget updated"
                    if (item.newAmount > (item.previousAmount ?: 0.0)) {
                        val difference = item.newAmount - (item.previousAmount ?: 0.0)
                        binding.tvBudgetChangeAmount.text = "+${CurrencyFormatter.formatLKR(difference)}"
                        binding.tvBudgetChangeAmount.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.positive_green)
                        )
                    } else {
                        val difference = (item.previousAmount ?: 0.0) - item.newAmount
                        binding.tvBudgetChangeAmount.text = "-${CurrencyFormatter.formatLKR(difference)}"
                        binding.tvBudgetChangeAmount.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.negative_red)
                        )
                    }
                }

                BudgetAction.DELETE_CATEGORY_BUDGET -> {
                    binding.tvBudgetChangeTitle.text = "${item.categoryName} budget deleted"
                    binding.tvBudgetChangeAmount.text = CurrencyFormatter.formatLKR((item.previousAmount ?: 0.0))
                    binding.tvBudgetChangeAmount.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.negative_red)
                    )
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<BudgetHistory>() {
        override fun areItemsTheSame(oldItem: BudgetHistory, newItem: BudgetHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BudgetHistory, newItem: BudgetHistory): Boolean {
            return oldItem == newItem
        }
    }
}