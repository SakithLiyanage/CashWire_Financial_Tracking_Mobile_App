package com.example.cashwire.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.cashwire.R
import com.example.cashwire.data.UserRepository
import com.example.cashwire.databinding.DialogEditProfileBinding
import com.example.cashwire.models.User

class EditProfileDialog(
    context: Context,
    private val user: User,
    private val onSaved: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogEditProfileBinding
    private val userRepository = UserRepository.getInstance(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the dialog width to match parent
        window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Pre-fill existing user data
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        binding.etPhone.setText(user.phoneNumber ?: "")

        // Set up save and cancel buttons
        binding.btnSave.setOnClickListener {
            saveUserData()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveUserData() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        // Validate inputs
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }

        if (!isValidEmail(email)) {
            binding.etEmail.error = "Invalid email format"
            return
        }

        // Save user data
        userRepository.updateUserName(name)
        userRepository.updateUserEmail(email)
        userRepository.updateUserPhone(if (phone.isEmpty()) null else phone)

        // Notify caller that data was saved
        onSaved()

        // Show success message and dismiss
        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}