package com.example.cashwire.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.cashwire.data.TransactionRepository
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.TransactionType
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class TransactionExportManager(private val context: Context) {

    private val transactionRepository = TransactionRepository.getInstance(context)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Export transactions as JSON file
     */
    fun exportAsJson(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            val transactions = transactionRepository.getAllTransactions()

            // Convert to JSON
            val jsonData = gson.toJson(transactions)

            // Generate file name based on date
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "CashWire_transactions_${dateFormat.format(Date())}.json"

            // Write to file
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { stream ->
                stream.write(jsonData.toByteArray())
            }

            onSuccess(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Export transactions as text file
     */
    fun exportAsText(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            val transactions = transactionRepository.getAllTransactions()

            // Format as text
            val textData = formatTransactionsAsText(transactions)

            // Generate file name based on date
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "CashWire_transactions_${dateFormat.format(Date())}.txt"

            // Write to file
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { stream ->
                stream.write(textData.toByteArray())
            }

            onSuccess(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Format transactions as human-readable text
     */
    private fun formatTransactionsAsText(transactions: List<Transaction>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sb = StringBuilder()

        sb.appendLine("CASHWIRE TRANSACTIONS REPORT")
        sb.appendLine("Generated on: 2025-04-22 07:07:36") // Current timestamp
        sb.appendLine("User: SakithLiyanage") // Current user login
        sb.appendLine("------------------------------------")
        sb.appendLine()

        // Group by year and month
        val groupedByMonth = transactions.sortedByDescending { it.date }
            .groupBy { transaction ->
                val cal = Calendar.getInstance()
                cal.timeInMillis = transaction.date
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}"
            }

        var totalIncome = 0.0
        var totalExpense = 0.0

        groupedByMonth.forEach { (yearMonth, monthTransactions) ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = monthTransactions.first().date
            val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)

            sb.appendLine("== $monthName ==")

            var monthlyIncome = 0.0
            var monthlyExpense = 0.0

            monthTransactions.forEach { transaction ->
                val date = dateFormat.format(Date(transaction.date))
                val amount = CurrencyFormatter.formatLKR(transaction.amount)
                val type = if (transaction.type == TransactionType.INCOME) "INCOME" else "EXPENSE"
                val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"

                sb.appendLine("$date | $type | ${transaction.title} | $sign$amount")

                if (transaction.type == TransactionType.INCOME) {
                    monthlyIncome += transaction.amount
                    totalIncome += transaction.amount
                } else {
                    monthlyExpense += transaction.amount
                    totalExpense += transaction.amount
                }

                if (transaction.notes != null) {
                    sb.appendLine("  Note: ${transaction.notes}")
                }
            }

            sb.appendLine()
            sb.appendLine("MONTHLY SUMMARY:")
            sb.appendLine("Income: +${CurrencyFormatter.formatLKR(monthlyIncome)}")
            sb.appendLine("Expenses: -${CurrencyFormatter.formatLKR(monthlyExpense)}")
            sb.appendLine("Balance: ${CurrencyFormatter.formatLKR(monthlyIncome - monthlyExpense)}")
            sb.appendLine()
            sb.appendLine("------------------------------------")
        }

        sb.appendLine()
        sb.appendLine("TOTAL SUMMARY:")
        sb.appendLine("Total Income: +${CurrencyFormatter.formatLKR(totalIncome)}")
        sb.appendLine("Total Expenses: -${CurrencyFormatter.formatLKR(totalExpense)}")
        sb.appendLine("Overall Balance: ${CurrencyFormatter.formatLKR(totalIncome - totalExpense)}")

        return sb.toString()
    }

    /**
     * Share an exported file
     */
    fun shareExportedFile(fileName: String) {
        try {
            val file = File(context.filesDir, fileName)
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                file
            )

            val mimeType = when {
                fileName.endsWith(".json") -> "application/json"
                fileName.endsWith(".txt") -> "text/plain"
                else -> "application/octet-stream"
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "CashWire Transactions Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Transaction Data"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}