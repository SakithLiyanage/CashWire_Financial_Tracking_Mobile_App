package com.example.cashwire.models

import java.io.Serializable

data class Budget(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable