package com.example.cashwire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashwire.R
import com.example.cashwire.databinding.FragmentTransactionsBinding

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

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

        // Set current date
        binding.tvTransactionDate.text = "April 2025"

        // Set transaction summary amounts
        binding.tvIncomeAmount.text = "$2,500.00"
        binding.tvExpensesAmount.text = "$1,143.25"
        binding.tvBalanceAmount.text = "$1,356.75"

        // Set up search functionality
        setupSearch()

        // Set up filter and sort buttons
        setupFilterAndSort()

        // Load transaction data
        loadTransactions()

        // Set FAB click listener
        binding.fabAddTransaction.setOnClickListener {
            // Show add transaction dialog or navigate to add transaction screen
        }
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
            // Show filter dialog
        }

        binding.btnSort.setOnClickListener {
            // Show sort options dialog
        }
    }

    private fun loadTransactions(query: String = "") {
        // This would load transactions with optional search query
        // Update UI with transactions or show empty state

        // For now, just toggle empty state visibility for demo
        val hasTransactions = query.isEmpty() // Show transactions if no query
        binding.recyclerTransactions.visibility = if (hasTransactions) View.VISIBLE else View.GONE
        binding.layoutEmptyState.visibility = if (!hasTransactions) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}