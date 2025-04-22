package com.example.cashwire.models

import android.graphics.Color
import com.example.cashwire.R

data class Category(
    val id: String,
    val name: String,
    val iconRes: Int,
    val colorHex: Int
)

// Predefined categories
object Categories {
    val expenseCategories = listOf(
        Category("food", "Food and Dining", R.drawable.ic_food, Color.parseColor("#FF9800")),
        Category("transport", "Transportation", R.drawable.ic_transport, Color.parseColor("#03A9F4")),
        Category("shopping", "Shopping", R.drawable.ic_shopping, Color.parseColor("#E91E63")),
        Category("bills", "Bills and Utilities", R.drawable.ic_bills, Color.parseColor("#673AB7")),
        Category("entertainment", "Entertainment", R.drawable.ic_entertainment, Color.parseColor("#4CAF50")),
        Category("health", "Health and Medical", R.drawable.ic_health, Color.parseColor("#F44336")),
        Category("education", "Education", R.drawable.ic_education, Color.parseColor("#009688")),
        Category("personal", "Personal Care", R.drawable.ic_personal, Color.parseColor("#795548")),
        Category("home", "Home", R.drawable.ic_home, Color.parseColor("#607D8B")),
        Category("other_expense", "Other Expense", R.drawable.ic_other, Color.parseColor("#9E9E9E"))
    )

    val incomeCategories = listOf(
        Category("salary", "Salary", R.drawable.ic_salary, Color.parseColor("#4CAF50")),
        Category("investment", "Investment", R.drawable.ic_investment, Color.parseColor("#FF9800")),
        Category("gift", "Gift", R.drawable.ic_gift, Color.parseColor("#E91E63")),
        Category("refund", "Refund", R.drawable.ic_refund, Color.parseColor("#03A9F4")),
        Category("other_income", "Other Income", R.drawable.ic_other, Color.parseColor("#9E9E9E"))
    )
}