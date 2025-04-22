package com.example.cashwire.models

import java.io.Serializable

data class Transaction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String,
    val categoryIconRes: Int, // Resource ID for icon
    val categoryColor: Int,    // Color for category
    val date: Long,            // Date as timestamp
    val type: TransactionType, // INCOME or EXPENSE
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

enum class TransactionType {
    INCOME, EXPENSE
}