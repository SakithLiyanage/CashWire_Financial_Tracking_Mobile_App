package com.example.cashwire.models

import java.io.Serializable

data class BudgetHistory(
    val id: String = java.util.UUID.randomUUID().toString(),
    val action: BudgetAction,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val previousAmount: Double? = null,
    val newAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

enum class BudgetAction {
    SET_MAIN_BUDGET,
    INCREASE_MAIN_BUDGET,
    DECREASE_MAIN_BUDGET,
    SET_CATEGORY_BUDGET,
    UPDATE_CATEGORY_BUDGET,
    DELETE_CATEGORY_BUDGET
}