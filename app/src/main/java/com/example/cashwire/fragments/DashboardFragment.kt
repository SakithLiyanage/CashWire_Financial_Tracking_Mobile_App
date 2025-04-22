package com.example.cashwire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashwire.R
import com.example.cashwire.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var currentDateTime: String? = null
    private var currentUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentDateTime = it.getString("CURRENT_DATETIME")
            currentUser = it.getString("CURRENT_USER")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the dashboard with user data
        initializeDashboard()

        // Set greeting
        updateGreeting()

        // Update app info
        updateAppInfo()
    }

    private fun initializeDashboard() {
        // Set balance information
        binding.tvTotalBalance.text = "$2,356.75"
        binding.tvIncome.text = "$2,500.00"
        binding.tvExpenses.text = "$143.25"

        // Set budget progress
        binding.progressBudget.progress = 10
        binding.tvBudgetPercent.text = "9.55%"
        binding.tvBudgetAmount.text = "$143.25 / $1,500.00"
    }

    private fun updateGreeting() {
        binding.tvGreeting.text = "Hello, ${currentUser?.split("@")?.get(0) ?: "User"}"

        val today = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
        binding.tvDateToday.text = today
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