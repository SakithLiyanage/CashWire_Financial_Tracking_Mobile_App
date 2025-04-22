package com.example.cashwire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashwire.R
import com.example.cashwire.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

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

        // Set current budget period
        binding.tvBudgetPeriod.text = "April 2025"

        // Set budget amounts
        binding.tvSpent.text = "$1,143.25"
        binding.tvBudgetTotal.text = "$1,500.00"
        binding.tvRemaining.text = "$356.75"
        binding.progressTotal.progress = 76 // 76% of budget used
        binding.tvBudgetDaysLeft.text = "9 days left in this month"

        // Set up month selection
        setupMonthSelection()

        // Set FAB click listener
        binding.fabAddBudget?.setOnClickListener {
            // Show add budget dialog or navigate to add budget screen
        }
    }

    private fun setupMonthSelection() {
        binding.chipGroupMonths?.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                // Handle month selection change
                when (checkedIds[0]) {
                    R.id.chipJan -> binding.tvBudgetPeriod.text = "January 2025"
                    R.id.chipFeb -> binding.tvBudgetPeriod.text = "February 2025"
                    R.id.chipMar -> binding.tvBudgetPeriod.text = "March 2025"
                    R.id.chipApr -> binding.tvBudgetPeriod.text = "April 2025"
                    R.id.chipMay -> binding.tvBudgetPeriod.text = "May 2025"
                    R.id.chipJun -> binding.tvBudgetPeriod.text = "June 2025"
                    R.id.chipJul -> binding.tvBudgetPeriod.text = "July 2025"
                    R.id.chipAug -> binding.tvBudgetPeriod.text = "August 2025"
                    R.id.chipSep -> binding.tvBudgetPeriod.text = "September 2025"
                    R.id.chipOct -> binding.tvBudgetPeriod.text = "October 2025"
                    R.id.chipNov -> binding.tvBudgetPeriod.text = "November 2025"
                    R.id.chipDec -> binding.tvBudgetPeriod.text = "December 2025"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}