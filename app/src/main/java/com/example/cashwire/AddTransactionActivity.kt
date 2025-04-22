package com.example.cashwire

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashwire.data.TransactionRepository
import com.example.cashwire.databinding.ActivityAddTransactionBinding
import com.example.cashwire.fragments.CategorySelectionBottomSheet
import com.example.cashwire.models.Category
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.TransactionType
import com.example.cashwire.models.Categories
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var repository: TransactionRepository

    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedCategory: Category? = null
    private var transactionType: TransactionType = TransactionType.EXPENSE
    private val currentDateTime = "2025-04-22 04:17:46" // Updated timestamp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = TransactionRepository.getInstance(this)

        // Initialize with default category
        selectedCategory = Categories.expenseCategories.first()
        updateCategoryUI()

        // Set current date
        // Parse the current date from timestamp
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(currentDateTime)
            if (date != null) {
                selectedDate.time = date
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        updateDateUI()

        setupListeners()
    }

    private fun setupListeners() {
        // Close button
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Transaction type tabs
        binding.tabLayoutTransactionType.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                transactionType = when (tab?.position) {
                    0 -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                // Update category selection based on transaction type
                selectedCategory = if (transactionType == TransactionType.INCOME) {
                    Categories.incomeCategories.first()
                } else {
                    Categories.expenseCategories.first()
                }
                updateCategoryUI()
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        // Select category
        binding.cardSelectCategory.setOnClickListener {
            showCategorySelection()
        }

        // Select date
        binding.cardSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Save transaction (toolbar button)
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }

        // Save transaction (fab button)
        binding.fabSaveTransaction.setOnClickListener {
            saveTransaction()
        }
    }

    private fun showCategorySelection() {
        val categories = if (transactionType == TransactionType.INCOME) {
            Categories.incomeCategories
        } else {
            Categories.expenseCategories
        }

        val bottomSheet = CategorySelectionBottomSheet.newInstance(categories)
        bottomSheet.onCategorySelectedListener = { category ->
            selectedCategory = category
            updateCategoryUI()
        }
        bottomSheet.show(supportFragmentManager, "CategorySelectionBottomSheet")
    }

    private fun showDatePicker() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
            updateDateUI()
        }, year, month, day).show()
    }

    private fun updateCategoryUI() {
        selectedCategory?.let {
            binding.ivCategoryIcon.setImageResource(it.iconRes)
            binding.cardCategoryIcon.setCardBackgroundColor(it.colorHex and 0x33FFFFFF)
            binding.ivCategoryIcon.setColorFilter(it.colorHex)
            binding.tvSelectedCategory.text = it.name
        }
    }

    private fun updateDateUI() {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        binding.tvSelectedDate.text = dateFormat.format(selectedDate.time)
    }

    private fun saveTransaction() {
        // Validate input
        val amountStr = binding.etAmount.text.toString()
        val title = binding.etTitle.text.toString()

        if (amountStr.isEmpty()) {
            binding.etAmount.error = "Please enter an amount"
            return
        }

        if (title.isEmpty()) {
            binding.etTitle.error = "Please enter a title" // Fixed: Using error instead of text
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            binding.etAmount.error = "Amount must be greater than 0"
            return
        }

        val category = selectedCategory ?: Categories.expenseCategories.first()
        val notes = binding.etNotes.text.toString()

        // Create and save transaction
        val transaction = Transaction(
            title = title,
            amount = amount,
            category = category.name,
            categoryIconRes = category.iconRes,
            categoryColor = category.colorHex,
            date = selectedDate.timeInMillis,
            type = transactionType,
            notes = notes.ifEmpty { null }
        )

        repository.saveTransaction(transaction)

        // Update budget status
        checkBudgetStatus()

        Snackbar.make(binding.root, "Transaction saved successfully", Snackbar.LENGTH_SHORT).show()
        finish()
    }

    private fun checkBudgetStatus() {
        val monthlyBudget = repository.getMonthlyBudget()
        if (monthlyBudget <= 0) return

        val currentMonthExpenses = repository.getCurrentMonthTransactions()
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val percentUsed = (currentMonthExpenses / monthlyBudget) * 100

        when {
            percentUsed >= 100 -> {
                showBudgetAlert("You've exceeded your monthly budget!")
            }
            percentUsed >= 80 -> {
                showBudgetAlert("You've used more than 80% of your monthly budget!")
            }
        }
    }

    private fun showBudgetAlert(message: String) {
        // In a real implementation, this would create a notification
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}