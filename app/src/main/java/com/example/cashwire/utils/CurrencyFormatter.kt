package com.example.cashwire.utils

import java.text.NumberFormat
import java.util.*

object CurrencyFormatter {

    // Format amount in Sri Lankan Rupees (LKR)
    fun formatLKR(amount: Double): String {
        return "LKR " + String.format("%,.2f", amount)
    }

    // Format amount with sign prefix based on transaction type
    fun formatLKRWithSign(amount: Double, isIncome: Boolean): String {
        val sign = if (isIncome) "+" else "-"
        return "$sign LKR " + String.format("%,.2f", amount)
    }

    // Alternative formatter using NumberFormat
    fun formatLKRLocalized(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("si", "LK"))
        return formatter.format(amount)
    }
}