package com.example.cashwire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashwire.R
import com.example.cashwire.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the current datetime
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateTime = "2025-04-22 02:39:41" // Using provided time
        binding.tvProfileLastUpdate.text = "Last updated: $currentDateTime"

        // Set up click listeners for profile actions
        setupClickListeners()

        // Set user information
        binding.tvUserName.text = "Sakith Liyanage"
        binding.tvUserEmail.text = "sakith1@gmail.com"
    }

    private fun setupClickListeners() {
        // Edit Profile
        binding.btnEditProfile.setOnClickListener {
            // Navigate to edit profile screen
        }

        // Personal Info
        binding.cardPersonalInfo.setOnClickListener {
            // Navigate to personal info screen
        }

        // Security
        binding.cardSecurity.setOnClickListener {
            // Navigate to security settings
        }

        /* Appearance toggle - commented out until we implement this view
        binding.switchAppearance.setOnCheckedChangeListener { _, isChecked ->
            // Toggle between light/dark theme
        }
        */

        // Currency selection
        binding.cardCurrency.setOnClickListener {
            // Show currency selection dialog
        }

        /* Notifications toggle - commented out until we implement this view
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Enable/disable notifications
        }
        */

        // Help & Support
        binding.cardHelp.setOnClickListener {
            // Navigate to help & support screen
        }

        // About
        binding.cardAbout.setOnClickListener {
            // Navigate to about screen or show about dialog
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            // Implement logout functionality
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}