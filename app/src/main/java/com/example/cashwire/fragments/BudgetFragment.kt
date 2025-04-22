package com.example.cashwire.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashwire.R
import com.example.cashwire.adapters.BudgetHistoryAdapter
import com.example.cashwire.adapters.CategoryBudgetAdapter
import com.example.cashwire.data.BudgetRepository
import com.example.cashwire.databinding.DialogCategoryBudgetBinding
import com.example.cashwire.databinding.DialogSetBudgetBinding
import com.example.cashwire.databinding.FragmentBudgetBinding
import com.example.cashwire.models.Budget
import com.example.cashwire.models.CategoryBudget
import com.example.cashwire.utils.CurrencyFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetRepository: BudgetRepository
    private lateinit var categoryBudgetAdapter: CategoryBudgetAdapter
    private lateinit var budgetHistoryAdapter: BudgetHistoryAdapter

    private val currentDateTime = "2025-04-22 05:09:49" // Latest timestamp
    private val currentUser = "SakithLiyanage" // User login

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        budgetRepository = BudgetRepository.getInstance(requireContext())

        setupRecyclerViews()
        setupButtonListeners()
        updateAppInfo()

        // Load budget data
        loadBudgetData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh budget data when the fragment resumes
        loadBudgetData()
    }

    private fun setupRecyclerViews() {
        // Category budgets
        categoryBudgetAdapter = CategoryBudgetAdapter { categoryBudget ->
            showEditCategoryBudgetDialog(categoryBudget)
        }
        binding.recyclerCategoryBudgets.adapter = categoryBudgetAdapter

        // Budget history
        budgetHistoryAdapter = BudgetHistoryAdapter()
        binding.recyclerBudgetHistory.adapter = budgetHistoryAdapter
    }

    private fun setupButtonListeners() {
        // Set main budget
        binding.btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        binding.tvEditTotalBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        // Add category budget
        binding.btnAddCategoryBudget.setOnClickListener {
            showAddCategoryBudgetDialog()
        }

        binding.btnStartCategoryBudget.setOnClickListener {
            showAddCategoryBudgetDialog()
        }
    }

    private fun loadBudgetData() {
        // Load main budget
        val mainBudget = budgetRepository.getMainBudget()
        if (mainBudget != null) {
            updateMainBudgetUI(mainBudget)
        } else {
            // Show default values if no budget set
            binding.tvTotalBudgetAmount.text = "රු 0.00"
            binding.tvTotalSpent.text = "රු 0.00"
            binding.tvRemainingBudget.text = "රු 0.00"
            binding.progressTotalBudget.progress = 0
            binding.tvBudgetStatus.text = "No budget set"
        }

        // Load category budgets
        val categoryBudgets = budgetRepository.getCategoryBudgets()
        if (categoryBudgets.isNotEmpty()) {
            binding.layoutEmptyCategories.visibility = View.GONE
            binding.recyclerCategoryBudgets.visibility = View.VISIBLE
            categoryBudgetAdapter.submitList(categoryBudgets)
        } else {
            binding.layoutEmptyCategories.visibility = View.VISIBLE
            binding.recyclerCategoryBudgets.visibility = View.GONE
        }

        // Load budget history
        val budgetHistory = budgetRepository.getBudgetHistory()
        budgetHistoryAdapter.submitList(budgetHistory)
    }

    private fun updateMainBudgetUI(budget: Budget) {
        // Format main budget amount
        binding.tvTotalBudgetAmount.text = CurrencyFormatter.formatLKR(budget.amount)

        // Calculate and format spent amount
        val spent = budgetRepository.getMonthlySpending()
        binding.tvTotalSpent.text = CurrencyFormatter.formatLKR(spent)

        // Calculate and format remaining amount
        val remaining = budgetRepository.getRemainingBudget()
        binding.tvRemainingBudget.text = CurrencyFormatter.formatLKR(remaining)

        // Update progress bar
        val usagePercentage = budgetRepository.getBudgetUsagePercentage()
        binding.progressTotalBudget.progress = usagePercentage
        binding.tvBudgetStatus.text = "$usagePercentage% of budget spent"

        // Update progress color based on percentage
        val progressColor = when {
            usagePercentage >= 90 -> R.color.negative_red
            usagePercentage >= 75 -> R.color.warning_orange
            else -> R.color.positive_green
        }
        binding.progressTotalBudget.setIndicatorColor(resources.getColor(progressColor, null))

        // Update remaining budget text color
        binding.tvRemainingBudget.setTextColor(resources.getColor(progressColor, null))
    }

    private fun showSetBudgetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogSetBudgetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Pre-fill with current budget if exists
        budgetRepository.getMainBudget()?.let { budget ->
            dialogBinding.etBudgetAmount.setText(budget.amount.toString())
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSaveBudget.setOnClickListener {
            val amountStr = dialogBinding.etBudgetAmount.text.toString()
            if (amountStr.isEmpty()) {
                dialogBinding.etBudgetAmount.error = "Please enter an amount"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                dialogBinding.etBudgetAmount.error = "Amount must be greater than 0"
                return@setOnClickListener
            }

            budgetRepository.setMainBudget(amount)
            loadBudgetData() // Refresh UI
            dialog.dismiss()

            Snackbar.make(binding.root, "Budget updated successfully", Snackbar.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showAddCategoryBudgetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogCategoryBudgetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Set up category selection
        dialogBinding.cardSelectCategory.setOnClickListener {
            showCategorySelection(dialogBinding)
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSaveCategoryBudget.setOnClickListener {
            val amountStr = dialogBinding.etCategoryBudgetAmount.text.toString()
            val categoryId = dialogBinding.cardSelectCategory.tag as? String

            if (categoryId == null) {
                Snackbar.make(dialogBinding.root, "Please select a category", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amountStr.isEmpty()) {
                dialogBinding.etCategoryBudgetAmount.error = "Please enter an amount"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                dialogBinding.etCategoryBudgetAmount.error = "Amount must be greater than 0"
                return@setOnClickListener
            }

            // Get the selected category and create budget
            val category = getSelectedCategory(categoryId)
            if (category != null) {
                val categoryBudget = CategoryBudget(
                    categoryId = categoryId,
                    categoryName = category.name,
                    categoryIconRes = category.iconRes,
                    categoryColor = category.colorHex,
                    amount = amount
                )

                budgetRepository.setCategoryBudget(categoryBudget)
                loadBudgetData() // Refresh UI
                dialog.dismiss()

                Snackbar.make(binding.root, "Category budget added", Snackbar.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showEditCategoryBudgetDialog(categoryBudget: CategoryBudget) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogCategoryBudgetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Set dialog as edit mode
        dialogBinding.tvDialogTitle.text = "Edit Category Budget"

        // Pre-fill with category budget data
        dialogBinding.tvSelectedCategory.text = categoryBudget.categoryName
        dialogBinding.ivCategoryIcon.setImageResource(categoryBudget.categoryIconRes)
        dialogBinding.cardCategoryIcon.setCardBackgroundColor(categoryBudget.categoryColor and 0x33FFFFFF)
        dialogBinding.ivCategoryIcon.setColorFilter(categoryBudget.categoryColor)
        dialogBinding.cardSelectCategory.tag = categoryBudget.categoryId
        dialogBinding.etCategoryBudgetAmount.setText(categoryBudget.amount.toString())

        // Disable category selection in edit mode
        dialogBinding.cardSelectCategory.isClickable = false

        // Add delete button
        dialogBinding.btnCancel.text = "Delete"
        dialogBinding.btnCancel.setTextColor(resources.getColor(R.color.negative_red, null))

        dialogBinding.btnCancel.setOnClickListener {
            // Show delete confirmation
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete the budget for ${categoryBudget.categoryName}?")
                .setPositiveButton("Delete") { _, _ ->
                    budgetRepository.deleteCategoryBudget(categoryBudget.categoryId)
                    loadBudgetData() // Refresh UI
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Category budget deleted", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        dialogBinding.btnSaveCategoryBudget.text = "Update"
        dialogBinding.btnSaveCategoryBudget.setOnClickListener {
            val amountStr = dialogBinding.etCategoryBudgetAmount.text.toString()

            if (amountStr.isEmpty()) {
                dialogBinding.etCategoryBudgetAmount.error = "Please enter an amount"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                dialogBinding.etCategoryBudgetAmount.error = "Amount must be greater than 0"
                return@setOnClickListener
            }

            // Update the category budget
            val updatedBudget = categoryBudget.copy(amount = amount, updatedAt = System.currentTimeMillis())
            budgetRepository.setCategoryBudget(updatedBudget)
            loadBudgetData() // Refresh UI
            dialog.dismiss()

            Snackbar.make(binding.root, "Category budget updated", Snackbar.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showCategorySelection(dialogBinding: DialogCategoryBudgetBinding) {
        val categories = com.example.cashwire.models.Categories.expenseCategories

        val items = categories.map { it.name }.toTypedArray()
        val icons = categories.map { it.iconRes }.toIntArray()
        val colors = categories.map { it.colorHex }.toIntArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select Category")
            .setItems(items) { _, which ->
                val selectedCategory = categories[which]

                // Update the UI
                dialogBinding.tvSelectedCategory.text = selectedCategory.name
                dialogBinding.ivCategoryIcon.setImageResource(selectedCategory.iconRes)
                dialogBinding.cardCategoryIcon.setCardBackgroundColor(selectedCategory.colorHex and 0x33FFFFFF)
                dialogBinding.ivCategoryIcon.setColorFilter(selectedCategory.colorHex)

                // Store the category ID as a tag
                dialogBinding.cardSelectCategory.tag = selectedCategory.id
            }
            .show()
    }

    private fun getSelectedCategory(categoryId: String): com.example.cashwire.models.Category? {
        return com.example.cashwire.models.Categories.expenseCategories.find { it.id == categoryId }
    }

    private fun updateAppInfo() {
        binding.tvLastUpdate.text = "Last updated: $currentDateTime"
        binding.tvUserLogin.text = "Logged in as: $currentUser"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}