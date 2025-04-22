package com.example.cashwire.data

import android.content.Context
import android.content.SharedPreferences
import com.example.cashwire.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class BudgetRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        TransactionRepository.PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val transactionRepository = TransactionRepository.getInstance(context)

    companion object {
        const val KEY_BUDGET = "key_budget"
        const val KEY_CATEGORY_BUDGETS = "key_category_budgets"
        const val KEY_BUDGET_HISTORY = "key_budget_history"

        @Volatile
        private var instance: BudgetRepository? = null

        fun getInstance(context: Context): BudgetRepository {
            return instance ?: synchronized(this) {
                instance ?: BudgetRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // Main Budget Methods
    fun setMainBudget(amount: Double) {
        val currentBudget = getMainBudget()
        val budgetAction = if (currentBudget == null) {
            BudgetAction.SET_MAIN_BUDGET
        } else if (amount > currentBudget.amount) {
            BudgetAction.INCREASE_MAIN_BUDGET
        } else {
            BudgetAction.DECREASE_MAIN_BUDGET
        }

        val budget = Budget(
            amount = amount,
            updatedAt = System.currentTimeMillis()
        )

        saveMainBudget(budget)

        // Add to history
        addBudgetHistory(
            BudgetHistory(
                action = budgetAction,
                previousAmount = currentBudget?.amount,
                newAmount = amount
            )
        )
    }

    fun getMainBudget(): Budget? {
        val json = sharedPreferences.getString(KEY_BUDGET, null) ?: return null
        return try {
            gson.fromJson(json, Budget::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveMainBudget(budget: Budget) {
        val json = gson.toJson(budget)
        sharedPreferences.edit().putString(KEY_BUDGET, json).apply()
    }

    // Category Budget Methods
    fun setCategoryBudget(categoryBudget: CategoryBudget) {
        val categoryBudgets = getCategoryBudgets().toMutableList()
        val existingIndex = categoryBudgets.indexOfFirst { it.categoryId == categoryBudget.categoryId }

        val budgetAction: BudgetAction
        var previousAmount: Double? = null

        if (existingIndex >= 0) {
            previousAmount = categoryBudgets[existingIndex].amount
            categoryBudgets[existingIndex] = categoryBudget.copy(updatedAt = System.currentTimeMillis())
            budgetAction = BudgetAction.UPDATE_CATEGORY_BUDGET
        } else {
            categoryBudgets.add(categoryBudget)
            budgetAction = BudgetAction.SET_CATEGORY_BUDGET
        }

        saveCategoryBudgets(categoryBudgets)

        // Add to history
        addBudgetHistory(
            BudgetHistory(
                action = budgetAction,
                categoryId = categoryBudget.categoryId,
                categoryName = categoryBudget.categoryName,
                previousAmount = previousAmount,
                newAmount = categoryBudget.amount
            )
        )
    }

    fun deleteCategoryBudget(categoryId: String) {
        val categoryBudgets = getCategoryBudgets().toMutableList()
        val existingBudget = categoryBudgets.find { it.categoryId == categoryId }

        if (existingBudget != null) {
            categoryBudgets.removeAll { it.categoryId == categoryId }
            saveCategoryBudgets(categoryBudgets)

            // Add to history
            addBudgetHistory(
                BudgetHistory(
                    action = BudgetAction.DELETE_CATEGORY_BUDGET,
                    categoryId = categoryId,
                    categoryName = existingBudget.categoryName,
                    previousAmount = existingBudget.amount,
                    newAmount = 0.0
                )
            )
        }
    }

    fun getCategoryBudgets(): List<CategoryBudget> {
        val json = sharedPreferences.getString(KEY_CATEGORY_BUDGETS, null) ?: return emptyList()
        val type: Type = object : TypeToken<List<CategoryBudget>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun saveCategoryBudgets(budgets: List<CategoryBudget>) {
        val json = gson.toJson(budgets)
        sharedPreferences.edit().putString(KEY_CATEGORY_BUDGETS, json).apply()
    }

    // Budget History Methods
    fun getBudgetHistory(): List<BudgetHistory> {
        val json = sharedPreferences.getString(KEY_BUDGET_HISTORY, null) ?: return emptyList()
        val type: Type = object : TypeToken<List<BudgetHistory>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun addBudgetHistory(history: BudgetHistory) {
        val historyItems = getBudgetHistory().toMutableList()
        historyItems.add(0, history) // Add new history at the beginning

        // Keep only the latest 100 history items to avoid excessive storage
        val trimmedHistory = if (historyItems.size > 100) {
            historyItems.take(100)
        } else {
            historyItems
        }

        val json = gson.toJson(trimmedHistory)
        sharedPreferences.edit().putString(KEY_BUDGET_HISTORY, json).apply()
    }

    // Budget Analysis Methods
    fun getMonthlySpending(): Double {
        return transactionRepository.getCurrentMonthTransactions()
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    // FIXED: Updated to match by category name, not ID
    fun getCategorySpending(categoryBudget: CategoryBudget): Double {
        return transactionRepository.getCurrentMonthTransactions()
            .filter {
                it.type == TransactionType.EXPENSE &&
                        // Match by category name since that's what's stored in transactions
                        it.category == categoryBudget.categoryName
            }
            .sumOf { it.amount }
    }

    fun getBudgetUsagePercentage(): Int {
        val budget = getMainBudget()?.amount ?: return 0
        val spent = getMonthlySpending()

        return if (budget > 0) {
            ((spent / budget) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    fun getCategoryBudgetUsage(categoryBudget: CategoryBudget): Int {
        val spent = getCategorySpending(categoryBudget)

        return if (categoryBudget.amount > 0) {
            ((spent / categoryBudget.amount) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    fun getRemainingBudget(): Double {
        val budget = getMainBudget()?.amount ?: 0.0
        val spent = getMonthlySpending()
        return (budget - spent).coerceAtLeast(0.0)
    }

    fun getBudgetStatus(): String {
        val percentage = getBudgetUsagePercentage()
        return when {
            percentage >= 90 -> "Budget almost depleted!"
            percentage >= 75 -> "Budget warning: Over 75% used"
            percentage >= 50 -> "Budget half spent"
            else -> "Budget on track"
        }
    }
}