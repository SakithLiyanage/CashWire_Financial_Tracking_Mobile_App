package com.example.cashwire.data

import android.content.Context
import android.content.SharedPreferences
import com.example.cashwire.models.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TransactionRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        const val PREFERENCES_NAME = "cashwire_preferences"
        const val KEY_TRANSACTIONS = "key_transactions"
        const val KEY_MONTHLY_BUDGET = "key_monthly_budget"
        const val KEY_CURRENCY = "key_currency"

        @Volatile
        private var instance: TransactionRepository? = null

        fun getInstance(context: Context): TransactionRepository {
            return instance ?: synchronized(this) {
                instance ?: TransactionRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // Save a new transaction
    fun saveTransaction(transaction: Transaction) {
        val transactions = getAllTransactions().toMutableList()
        transactions.add(transaction)
        saveAllTransactions(transactions)
    }

    // Update an existing transaction
    fun updateTransaction(transaction: Transaction) {
        val transactions = getAllTransactions().toMutableList()
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            transactions[index] = transaction
            saveAllTransactions(transactions)
        }
    }

    // Delete a transaction
    fun deleteTransaction(transactionId: String) {
        val transactions = getAllTransactions().toMutableList()
        transactions.removeAll { it.id == transactionId }
        saveAllTransactions(transactions)
    }

    // Get all transactions
    fun getAllTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        val type: Type = object : TypeToken<List<Transaction>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Get transactions for current month
    fun getCurrentMonthTransactions(): List<Transaction> {
        val currentMonth = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        return getAllTransactions().filter { it.date >= currentMonth }
    }

    // Save all transactions
    private fun saveAllTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    // Set monthly budget
    fun setMonthlyBudget(budget: Double) {
        sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
    }

    // Get monthly budget
    fun getMonthlyBudget(): Double {
        return sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    // Set preferred currency
    fun setCurrency(currencyCode: String) {
        sharedPreferences.edit().putString(KEY_CURRENCY, currencyCode).apply()
    }

    // Get preferred currency
    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, "LKR") ?: "LKR" // Changed default currency to LKR
    }

    // Export transactions as JSON
    fun exportTransactions(): String {
        val transactions = getAllTransactions()
        return gson.toJson(transactions)
    }

    // Import transactions from JSON
    fun importTransactions(json: String) {
        try {
            val type: Type = object : TypeToken<List<Transaction>>() {}.type
            val transactions: List<Transaction> = gson.fromJson(json, type)
            saveAllTransactions(transactions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}