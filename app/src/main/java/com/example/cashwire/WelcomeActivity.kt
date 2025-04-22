package com.example.cashwire.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.cashwire.R
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Hide system UI for immersive experience
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // Initialize views
        val logo = findViewById<View>(R.id.ivLogo)
        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val taglineText = findViewById<TextView>(R.id.tvTagline)
        val buttonCard = findViewById<CardView>(R.id.cardButtons)
        val loginButton = findViewById<MaterialButton>(R.id.btnLogin)
        val signUpButton = findViewById<MaterialButton>(R.id.btnSignUp)


        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Apply animations with sequence
        logo.startAnimation(fadeIn)

        welcomeText.alpha = 0f
        welcomeText.postDelayed({
            welcomeText.alpha = 1f
            welcomeText.startAnimation(slideUp)
        }, 300)

        taglineText.alpha = 0f
        taglineText.postDelayed({
            taglineText.alpha = 1f
            taglineText.startAnimation(slideUp)
        }, 500)

        buttonCard.alpha = 0f
        buttonCard.postDelayed({
            buttonCard.alpha = 1f
            buttonCard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_slide_up))
        }, 700)

        // Set up button click listeners
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


    }
}