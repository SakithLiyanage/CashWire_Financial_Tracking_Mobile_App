package com.example.cashwire.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashwire.R
import com.example.cashwire.auth.LoginActivity
import com.example.cashwire.data.UserRepository
import com.example.cashwire.databinding.FragmentProfileBinding
import com.example.cashwire.dialogs.EditProfileDialog
import com.example.cashwire.models.User
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository
    private lateinit var currentUser: User

    private val currentDateTime = "2025-04-22 06:39:42" // Updated timestamp
    private val currentUserLogin = "SakithLiyanage" // User login

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

        userRepository = UserRepository.getInstance(requireContext())

        // Load user data
        loadUserData()

        // Update timestamp
        binding.tvProfileLastUpdate.text = "Last updated: $currentDateTime"

        // Set up click listeners
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Reload user data in case it was updated
        loadUserData()
    }

    private fun loadUserData() {
        currentUser = userRepository.getCurrentUser()

        // Set user information - FIXED: Use the actual name from User object
        binding.tvUserName.text = currentUser.name
        binding.tvUserEmail.text = currentUser.email
    }

    private fun setupClickListeners() {
        // Edit profile button
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Personal information card
        binding.cardPersonalInfo.setOnClickListener {
            showEditProfileDialog()
        }



        // Logout button
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showEditProfileDialog() {
        val dialog = EditProfileDialog(
            requireContext(),
            currentUser,
            onSaved = {
                loadUserData() // Reload user data after saving
            }
        )
        dialog.show()
    }

    private fun showSecurityDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Security Settings")
            .setMessage("Security features will be available in the next version of CashWire.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        userRepository.logout()

        // Navigate to login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}