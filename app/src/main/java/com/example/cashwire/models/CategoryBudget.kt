package com.example.cashwire.models

import java.io.Serializable

data class CategoryBudget(
    val id: String = java.util.UUID.randomUUID().toString(),
    val categoryId: String,
    val categoryName: String,
    val categoryIconRes: Int,
    val categoryColor: Int,
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable