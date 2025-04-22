package com.example.cashwire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashwire.R
import com.example.cashwire.adapters.CategoryTransactionsAdapter
import com.example.cashwire.data.TransactionRepository
import com.example.cashwire.databinding.FragmentTransactionsBinding
import com.example.cashwire.models.CategoryTransactions
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.TransactionType
import com.example.cashwire.utils.CurrencyFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: TransactionRepository
    private lateinit var adapter: CategoryTransactionsAdapter

    private val currentDateTime = "2025-04-22 04:59:28" // Updated timestamp
    private val currentUser = "SakithLiyanagee" // Updated user login

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = TransactionRepository.getInstance(requireContext())

        // Set current date
        binding.tvTransactionDate.text = "April 2025"

        // Set up recycler view
        setupRecyclerView()

        // Set up search functionality
        setupSearch()

        // Set up filter and sort buttons
        setupFilterAndSort()

        // Load transaction data
        loadTransactions()

        // Set FAB click listener
        binding.fabAddTransaction.setOnClickListener {
            // Navigate to add transaction screen
            val intent = android.content.Intent(requireContext(), com.example.cashwire.AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload transactions when returning to this fragment
        loadTransactions()
    }

    private fun setupRecyclerView() {
        adapter = CategoryTransactionsAdapter("LKR") // Explicitly passing LKR as currency
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTransactions.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { textView: TextView, actionId: Int, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Perform search
                loadTransactions(textView.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupFilterAndSort() {
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }
    }

    // IMPLEMENTING MISSING METHODS

    // Show filter dialog
    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter_transactions, null)
        dialog.setContentView(dialogView)

        // Set up filter options
        val btnIncome = dialogView.findViewById<View>(R.id.btnIncome)
        val btnExpense = dialogView.findViewById<View>(R.id.btnExpense)
        val btnAll = dialogView.findViewById<View>(R.id.btnAll)
        val btnApply = dialogView.findViewById<View>(R.id.btnApply)

        btnIncome.setOnClickListener {
            loadTransactions(filterType = "INCOME")
            dialog.dismiss()
        }

        btnExpense.setOnClickListener {
            loadTransactions(filterType = "EXPENSE")
            dialog.dismiss()
        }

        btnAll.setOnClickListener {
            loadTransactions()
            dialog.dismiss()
        }

        btnApply.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Show sort dialog
    private fun showSortDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_sort_transactions, null)
        dialog.setContentView(dialogView)

        // Set up sort options
        val btnDateNewest = dialogView.findViewById<View>(R.id.btnDateNewest)
        val btnDateOldest = dialogView.findViewById<View>(R.id.btnDateOldest)
        val btnAmountHighest = dialogView.findViewById<View>(R.id.btnAmountHighest)
        val btnAmountLowest = dialogView.findViewById<View>(R.id.btnAmountLowest)

        btnDateNewest.setOnClickListener {
            loadTransactions(sortBy = "DATE_DESC")
            dialog.dismiss()
        }

        btnDateOldest.setOnClickListener {
            loadTransactions(sortBy = "DATE_ASC")
            dialog.dismiss()
        }

        btnAmountHighest.setOnClickListener {
            loadTransactions(sortBy = "AMOUNT_DESC")
            dialog.dismiss()
        }

        btnAmountLowest.setOnClickListener {
            loadTransactions(sortBy = "AMOUNT_ASC")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadTransactions(query: String = "", filterType: String = "", sortBy: String = "DATE_DESC") {
        var transactions = repository.getAllTransactions()

        // Apply filter
        transactions = when (filterType) {
            "INCOME" -> transactions.filter { it.type == TransactionType.INCOME }
            "EXPENSE" -> transactions.filter { it.type == TransactionType.EXPENSE }
            else -> transactions
        }

        // Apply search query
        if (query.isNotEmpty()) {
            transactions = transactions.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        (it.notes ?: "").contains(query, ignoreCase = true)
            }
        }

        // Apply sorting
        transactions = when (sortBy) {
            "DATE_ASC" -> transactions.sortedBy { it.date }
            "DATE_DESC" -> transactions.sortedByDescending { it.date }
            "AMOUNT_ASC" -> transactions.sortedBy { it.amount }
            "AMOUNT_DESC" -> transactions.sortedByDescending { it.amount }
            else -> transactions.sortedByDescending { it.date }
        }

        // Group by category
        val categorizedTransactions = transactions.groupBy { it.category }
            .map { (category, transactions) ->
                CategoryTransactions(category, transactions)
            }

        // Update UI
        updateTransactionsSummary(transactions)

        // Update adapter data
        adapter.updateData(categorizedTransactions)

        // Toggle empty state visibility
        val hasTransactions = transactions.isNotEmpty()
        binding.recyclerTransactions.visibility = if (hasTransactions) View.VISIBLE else View.GONE
        binding.layoutEmptyState.visibility = if (!hasTransactions) View.VISIBLE else View.GONE
    }

    private fun updateTransactionsSummary(transactions: List<Transaction>) {
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val balance = income - expenses

        binding.tvIncomeAmount.text = formatCurrency(income)
        binding.tvExpensesAmount.text = formatCurrency(expenses)
        binding.tvBalanceAmount.text = formatCurrency(balance)
    }

    // Format currency to LKR
    private fun formatCurrency(amount: Double): String {
        return "LKR " + String.format("%,.2f", amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}