package com.example.cashwire.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.cashwire.HomeActivity
import com.example.cashwire.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var cbRememberMe: CheckBox
    private lateinit var rootView: ConstraintLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("cashwire_prefs", Context.MODE_PRIVATE)

        // Initialize views
        rootView = findViewById(R.id.rootContainer)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        btnLogin = findViewById(R.id.btnLogin)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        // Apply entrance animations
        animateElements()

        // Check if credentials are saved
        if (sharedPreferences.getBoolean("remember_me", false)) {
            etEmail.setText(sharedPreferences.getString("saved_email", ""))
            etPassword.setText(sharedPreferences.getString("saved_password", ""))
            cbRememberMe.isChecked = true
        }

        // Add text watchers to clear errors on typing
        setupTextWatchers()

        // Set up click listeners
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnLogin.setOnClickListener {
            if (validateInputs()) {
                // Show loading state
                showLoading(true)

                // Simulate API call delay
                btnLogin.postDelayed({
                    // Verify login against stored user data
                    if (verifyLogin(etEmail.text.toString().trim(), etPassword.text.toString())) {
                        // Save credentials if remember me is checked
                        saveCredentials()

                        // Save login status
                        saveLoginStatus(true)

                        // Hide loading
                        showLoading(false)

                        // Show success message
                        showSnackbar("Login successful")

                        // Navigate to dashboard after a short delay
                        rootView.postDelayed({
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finish()
                        }, 1000)
                    } else {
                        // Hide loading
                        showLoading(false)

                        // Show error message
                        tilPassword.error = "Invalid email or password"
                        tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                    }
                }, 1500)
            }
        }

        tvForgotPassword.setOnClickListener {
            // Simple animation feedback
            it.animate().alpha(0.5f).setDuration(100).withEndAction {
                it.animate().alpha(1f).setDuration(100).start()
            }.start()

            showSnackbar("Password reset feature coming soon!")
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

        tilEmail.alpha = 0f
        tilEmail.postDelayed({
            tilEmail.alpha = 1f
            tilEmail.startAnimation(slideUp)
        }, 400)

        tilPassword.alpha = 0f
        tilPassword.postDelayed({
            tilPassword.alpha = 1f
            tilPassword.startAnimation(slideUp)
        }, 500)

        cbRememberMe.alpha = 0f
        cbRememberMe.postDelayed({
            cbRememberMe.alpha = 1f
            cbRememberMe.startAnimation(fadeIn)
        }, 600)

        btnLogin.alpha = 0f
        btnLogin.postDelayed({
            btnLogin.alpha = 1f
            btnLogin.startAnimation(slideUp)
        }, 700)
    }

    private fun setupTextWatchers() {
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                tilEmail.error = null
            }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                tilPassword.error = null
            }
        })
    }

    private fun validateInputs(): Boolean {
        var isValid = true

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
            else -> {
                tilEmail.error = null
            }
        }

        // Validate password
        val password = etPassword.text.toString()
        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            tilPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
            isValid = false
        } else {
            tilPassword.error = null
        }

        return isValid
    }

    private fun verifyLogin(email: String, password: String): Boolean {
        val usersJson = sharedPreferences.getString("users", null) ?: return false

        try {
            val usersArray = org.json.JSONArray(usersJson)
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email &&
                    user.getString("password") == password) {
                    // Store current user details
                    val editor = sharedPreferences.edit()
                    editor.putString("current_user_name", user.getString("fullName"))
                    editor.putString("current_user_email", email)
                    editor.apply()
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun saveCredentials() {
        val editor = sharedPreferences.edit()
        if (cbRememberMe.isChecked) {
            editor.putString("saved_email", etEmail.text.toString().trim())
            editor.putString("saved_password", etPassword.text.toString())
            editor.putBoolean("remember_me", true)
        } else {
            editor.remove("saved_email")
            editor.remove("saved_password")
            editor.putBoolean("remember_me", false)
        }
        editor.apply()
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)

        // Store user data if logged in
        if (isLoggedIn) {
            editor.putString("current_user_email", etEmail.text.toString().trim())
            editor.putString("last_login_time", System.currentTimeMillis().toString())
        }
        editor.apply()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            btnLogin.isEnabled = false
            btnLogin.text = "LOGGING IN..."
            // Show progress animation
            btnLogin.icon = ContextCompat.getDrawable(this, R.drawable.ic_loading)
        } else {
            btnLogin.isEnabled = true
            btnLogin.text = "LOG IN"
            btnLogin.icon = null
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(ContextCompat.getColor(this@LoginActivity, R.color.blue))
            setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.white))
            show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}