package com.example.cashwire.models

data class CategoryTransactions(
    val category: String,
    val transactions: List<Transaction>
)