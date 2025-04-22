package com.example.cashwire.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashwire.R
import com.example.cashwire.adapters.RecentTransactionsAdapter
import com.example.cashwire.data.BudgetRepository
import com.example.cashwire.data.TransactionRepository
import com.example.cashwire.databinding.FragmentDashboardBinding
import com.example.cashwire.models.Transaction
import com.example.cashwire.models.TransactionType
import com.example.cashwire.utils.BackupRestoreManager
import com.example.cashwire.utils.CurrencyFormatter
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var currentDateTime: String? = null
    private var currentUser: String? = null

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var budgetRepository: BudgetRepository
    private lateinit var backupManager: BackupRestoreManager
    private lateinit var recentTransactionsAdapter: RecentTransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentDateTime = it.getString("CURRENT_DATETIME", "2025-04-22 11:25:29") // Updated timestamp
            currentUser = it.getString("CURRENT_USER", "SakithLiyanage") // Updated user login
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionRepository = TransactionRepository.getInstance(requireContext())
        budgetRepository = BudgetRepository.getInstance(requireContext())
        backupManager = BackupRestoreManager(requireContext())

        // Initialize adapters
        recentTransactionsAdapter = RecentTransactionsAdapter()
        binding.recyclerRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecentTransactions.adapter = recentTransactionsAdapter

        // Set greeting and date
        updateGreeting()

        // Load financial summary
        loadFinancialSummary()

        // Load recent transactions
        loadRecentTransactions()

        // Load budget data
        loadBudgetData()

        // Setup backup and restore buttons
        setupQuickActions()

        // Update user info and app info
        updateAppInfo()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this fragment
        loadFinancialSummary()
        loadRecentTransactions()
        loadBudgetData()
    }

    /**
     * Update the greeting based on time of day and user's full name
     */
    private fun updateGreeting() {
        // Use the full name instead of just the first name
        val userName = currentUser ?: "User"

        // Set greeting based on time of day
        val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }

        binding.tvGreeting.text = "$greeting, $userName!"

        // Set current date
        val today = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
        binding.tvDateToday.text = today
    }

    /**
     * Load and display financial summary (income, expenses, balance)
     */
    private fun loadFinancialSummary() {
        val transactions = transactionRepository.getCurrentMonthTransactions()

        // Calculate income, expenses and balance
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val balance = income - expenses

        // Update UI
        binding.tvIncomeAmount.text = CurrencyFormatter.formatLKR(income)
        binding.tvExpensesAmount.text = CurrencyFormatter.formatLKR(expenses)
        binding.tvBalanceAmount.text = CurrencyFormatter.formatLKR(balance)
    }

    /**
     * Load and display recent transactions
     */
    private fun loadRecentTransactions() {
        val transactions = transactionRepository.getAllTransactions()
            .sortedByDescending { it.date }
            .take(5) // Only show 5 most recent transactions

        if (transactions.isNotEmpty()) {
            binding.recyclerRecentTransactions.visibility = View.VISIBLE
            binding.tvNoRecentTransactions.visibility = View.GONE
            recentTransactionsAdapter.submitList(transactions)
        } else {
            binding.recyclerRecentTransactions.visibility = View.GONE
            binding.tvNoRecentTransactions.visibility = View.VISIBLE
        }
    }

    /**
     * Load and display budget information
     */
    private fun loadBudgetData() {
        val budget = budgetRepository.getMainBudget()

        if (budget != null) {
            // Get monthly spending and calculate percentage
            val spent = budgetRepository.getMonthlySpending()
            val percentage = budgetRepository.getBudgetUsagePercentage()
            val remaining = budgetRepository.getRemainingBudget()

            // Update UI
            binding.tvBudgetAmount.text = CurrencyFormatter.formatLKR(budget.amount)
            binding.tvBudgetSpent.text = "${CurrencyFormatter.formatLKR(spent)} spent"
            binding.tvBudgetRemaining.text = "${CurrencyFormatter.formatLKR(remaining)} remaining"
            binding.progressBudget.progress = percentage
            binding.tvBudgetPercentage.text = "$percentage%"

            binding.cardBudget.visibility = View.VISIBLE
            binding.cardNoBudget.visibility = View.GONE
        } else {
            // Show "No Budget Set" card
            binding.cardBudget.visibility = View.GONE
            binding.cardNoBudget.visibility = View.VISIBLE
        }
    }

    /**
     * Set up quick action buttons - backup and restore
     */
    private fun setupQuickActions() {
        // Backup Data button
        binding.btnBackupData.setOnClickListener {
            // Show dialog to choose between JSON and text backup
            backupManager.showBackupFormatDialog(
                onSuccess = { fileName ->
                    Snackbar.make(binding.root, "Data backed up successfully: $fileName", Snackbar.LENGTH_LONG)
                        .setAction("Share") {
                            backupManager.shareBackupFile(fileName)
                        }
                        .show()
                },
                onError = { error ->
                    Snackbar.make(binding.root, "Backup failed: $error", Snackbar.LENGTH_SHORT).show()
                }
            )
        }

        // Restore Data button
        binding.btnRestoreData.setOnClickListener {
            backupManager.showRestoreDialog(
                onRestoreComplete = { success ->
                    if (success) {
                        Snackbar.make(binding.root, "Data restored successfully", Snackbar.LENGTH_SHORT).show()
                        // Refresh data after restore
                        loadFinancialSummary()
                        loadRecentTransactions()
                        loadBudgetData()
                    } else {
                        Snackbar.make(binding.root, "Data restore failed", Snackbar.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Set budget button in case of no budget
        binding.btnSetBudget?.setOnClickListener {
            // Navigate to Budget fragment
            requireActivity().findViewById<View>(R.id.navigation_budget).performClick()
        }
    }

    /**
     * Update app information display
     */
    private fun updateAppInfo() {
        binding.tvLastUpdate.text = "Last updated: $currentDateTime"
        binding.tvUserLogin.text = "Logged in as: $currentUser"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}