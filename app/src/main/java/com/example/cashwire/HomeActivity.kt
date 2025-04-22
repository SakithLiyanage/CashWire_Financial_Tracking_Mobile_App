package com.example.cashwire

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cashwire.databinding.ActivityHomeBinding
import com.example.cashwire.fragments.BudgetFragment
import com.example.cashwire.fragments.DashboardFragment
import com.example.cashwire.fragments.ProfileFragment
import com.example.cashwire.fragments.TransactionsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val currentDateTime = "2025-04-22 04:50:33" // Updated timestamp
    private val currentUserLogin = "SakithLiyanage" // User login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(DashboardFragment().apply {
                        arguments = Bundle().apply {
                            putString("CURRENT_DATETIME", currentDateTime)
                            putString("CURRENT_USER", currentUserLogin)
                        }
                    })
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_transactions -> {
                    loadFragment(TransactionsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_budget -> {
                    loadFragment(BudgetFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        // Set up fab for adding transactions
        binding.fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }

        // Load default fragment (Dashboard)
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showAddTransactionDialog() {
        val intent = Intent(this, AddTransactionActivity::class.java)
        startActivity(intent)
    }
}