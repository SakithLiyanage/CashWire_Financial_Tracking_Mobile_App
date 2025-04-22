package com.example.cashwire.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cashwire.R
import com.example.cashwire.databinding.ItemTransactionBinding
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.TransactionType
import com.example.cashwire.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val currencyCode: String = "LKR") :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions = listOf<Transaction>()

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTransactionName.text = transaction.title

            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvTransactionDate.text = dateFormat.format(Date(transaction.date))

            // Format amount and set color based on transaction type
            val isIncome = transaction.type == TransactionType.INCOME
            val formattedAmount = when (transaction.type) {
                TransactionType.INCOME -> "+LKR ${String.format("%,.2f", transaction.amount)}"
                TransactionType.EXPENSE -> "LKR ${String.format("%,.2f", transaction.amount)}"
            }

            binding.tvAmount.text = formattedAmount

            binding.tvAmount.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    when (transaction.type) {
                        TransactionType.INCOME -> R.color.positive_green
                        TransactionType.EXPENSE -> R.color.negative_red
                    }
                )
            )

            // Set category icon and background
            binding.ivCategoryIcon.setImageResource(transaction.categoryIconRes)
            binding.cardCategoryIcon.setCardBackgroundColor(transaction.categoryColor and 0x33FFFFFF)
            binding.ivCategoryIcon.setColorFilter(transaction.categoryColor)
        }
    }
}