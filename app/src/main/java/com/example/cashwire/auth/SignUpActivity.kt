package com.example.cashwire.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.cashwire.HomeActivity
import com.example.cashwire.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var btnSignUp: MaterialButton
    private lateinit var rootView: ConstraintLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("cashwire_prefs", Context.MODE_PRIVATE)

        // Initialize views
        rootView = findViewById(R.id.rootContainer)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tilFullName = findViewById(R.id.tilFullName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        // Apply animations
        animateElements()

        // Add text watchers to clear errors
        setupTextWatchers()

        // Set up click listeners
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnSignUp.setOnClickListener {
            if (validateInputs()) {
                // Show loading state
                showLoading(true)

                // Simulate API call delay
                btnSignUp.postDelayed({
                    // Save user data
                    saveUserData()

                    // Save login status
                    saveLoginStatus(true)

                    // Hide loading
                    showLoading(false)

                    // Show success message
                    showSnackbar("Account created successfully!")

                    // Navigate to dashboard after a short delay
                    rootView.postDelayed({
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }, 1000)
                }, 1500)
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun animateElements() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        val title = findViewById<TextView>(R.id.tvTitle)
        val subtitle = findViewById<TextView>(R.id.tvSubtitle)

        title.startAnimation(fadeIn)

        subtitle.alpha = 0f
        subtitle.postDelayed({
            subtitle.alpha = 1f
            subtitle.startAnimation(slideUp)
        }, 300)

        tilFullName.alpha = 0f
        tilFullName.postDelayed({
            tilFullName.alpha = 1f
            tilFullName.startAnimation(slideUp)
        }, 400)

        tilEmail.alpha = 0f
        tilEmail.postDelayed({
            tilEmail.alpha = 1f
            tilEmail.startAnimation(slideUp)
        }, 500)

        tilPassword.alpha = 0f
        tilPassword.postDelayed({
            tilPassword.alpha = 1f
            tilPassword.startAnimation(slideUp)
        }, 600)

        tilConfirmPassword.alpha = 0f
        tilConfirmPassword.postDelayed({
            tilConfirmPassword.alpha = 1f
            tilConfirmPassword.startAnimation(slideUp)
        }, 700)

        btnSignUp.alpha = 0f
        btnSignUp.postDelayed({
            btnSignUp.alpha = 1f
            btnSignUp.startAnimation(slideUp)
        }, 800)
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when (currentFocus) {
                    etFullName -> tilFullName.error = null
                    etEmail -> tilEmail.error = null
                    etPassword -> tilPassword.error = null
                    etConfirmPassword -> tilConfirmPassword.error = null
                }
            }
        }

        etFullName.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)
        etConfirmPassword.addTextChangedListener(textWatcher)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate full name
        val fullName = etFullName.text.toString().trim()
        when {
            fullName.isEmpty() -> {
                tilFullName.error = "Name is required"
                tilFullName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            fullName.length < 3 -> {
                tilFullName.error = "Name must be at least 3 characters"
                tilFullName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            else -> {
                tilFullName.error = null
            }
        }

        // Validate email
        val email = etEmail.text.toString().trim()
        when {
            email.isEmpty() -> {
                tilEmail.error = "Email is required"
                tilEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                tilEmail.error = "Enter a valid email address"
                tilEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            isEmailRegistered(email) -> {
                tilEmail.error = "Email already registered"
                tilEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            else -> {
                tilEmail.error = null
            }
        }

        // Validate password
        val password = etPassword.text.toString()
        when {
            password.isEmpty() -> {
                tilPassword.error = "Password is required"
                tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            password.length < 6 -> {
                tilPassword.error = "Password must be at least 6 characters"
                tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            !password.matches(".*[A-Z].*".toRegex()) -> {
                tilPassword.error = "Password must contain at least one uppercase letter"
                tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            else -> {
                tilPassword.error = null
            }
        }

        // Validate confirm password
        val confirmPassword = etConfirmPassword.text.toString()
        when {
            confirmPassword.isEmpty() -> {
                tilConfirmPassword.error = "Please confirm your password"
                tilConfirmPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            confirmPassword != password -> {
                tilConfirmPassword.error = "Passwords don't match"
                tilConfirmPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                isValid = false
            }
            else -> {
                tilConfirmPassword.error = null
            }
        }

        return isValid
    }

    private fun isEmailRegistered(email: String): Boolean {
        val usersJson = sharedPreferences.getString("users", null) ?: return false

        try {
            val usersArray = JSONArray(usersJson)
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun saveUserData() {
        try {
            // Get existing users or create new array
            val usersJson = sharedPreferences.getString("users", null)
            val usersArray = if (usersJson != null) JSONArray(usersJson) else JSONArray()

            // Create new user object
            val newUser = JSONObject().apply {
                put("fullName", etFullName.text.toString().trim())
                put("email", etEmail.text.toString().trim())
                put("password", etPassword.text.toString()) // In a real app, you would hash this
                put("dateCreated", System.currentTimeMillis())
                put("accountType", "standard")
            }

            // Add to array and save
            usersArray.put(newUser)

            val editor = sharedPreferences.edit()
            editor.putString("users", usersArray.toString())
            editor.apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)

        // Save current user info
        if (isLoggedIn) {
            editor.putString("current_user_email", etEmail.text.toString().trim())
            editor.putString("current_user_name", etFullName.text.toString().trim())
            editor.putString("last_login_time", System.currentTimeMillis().toString())
        }

        editor.apply()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            btnSignUp.isEnabled = false
            btnSignUp.text = "CREATING ACCOUNT..."
            btnSignUp.icon = ContextCompat.getDrawable(this, R.drawable.ic_loading)
        } else {
            btnSignUp.isEnabled = true
            btnSignUp.text = "CREATE ACCOUNT"
            btnSignUp.icon = null
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(ContextCompat.getColor(this@SignUpActivity, R.color.teal))
            setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.white))
            show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}