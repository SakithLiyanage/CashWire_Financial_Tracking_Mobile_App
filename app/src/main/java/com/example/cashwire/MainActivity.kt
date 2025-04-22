package com.example.cashwire

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.cashwire.onboarding.OnboardingActivity
import com.google.android.material.progressindicator.LinearProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var logoContainer: CardView
    private lateinit var brandNameText: TextView
    private lateinit var taglineText: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var versionText: TextView
    private lateinit var circle1: View
    private lateinit var circle2: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide system UI for immersive experience
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        // Initialize views
        logoContainer = findViewById(R.id.logoContainer)
        brandNameText = findViewById(R.id.tvBrandName)
        taglineText = findViewById(R.id.tvTagline)
        progressBar = findViewById(R.id.progressBar)
        versionText = findViewById(R.id.tvVersion)
        circle1 = findViewById(R.id.circleDecor1)
        circle2 = findViewById(R.id.circleDecor2)

        // Start animations
        Handler(Looper.getMainLooper()).postDelayed({
            startLogoAnimation()
        }, 300)

        // Navigate to dashboard after animations complete
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, 3800)
    }

    private fun startLogoAnimation() {
        // Animate background circles
        animateBackgroundElements()

        // Animate logo container
        val logoFadeIn = ObjectAnimator.ofFloat(logoContainer, "alpha", 0f, 1f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }

        val logoScale = ObjectAnimator.ofFloat(logoContainer, "scaleX", 0.6f, 1f).apply {
            duration = 800
            interpolator = AnticipateOvershootInterpolator()
        }

        val logoScaleY = ObjectAnimator.ofFloat(logoContainer, "scaleY", 0.6f, 1f).apply {
            duration = 800
            interpolator = AnticipateOvershootInterpolator()
        }

        val logoSet = AnimatorSet().apply {
            playTogether(logoFadeIn, logoScale, logoScaleY)
            start()
        }

        // Animate text elements with sequence
        Handler(Looper.getMainLooper()).postDelayed({
            animateTextElements()
        }, 600)

        // Animate progress bar
        Handler(Looper.getMainLooper()).postDelayed({
            animateProgressBar()
        }, 1000)

        // Animate version text
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofFloat(versionText, "alpha", 0f, 1f).apply {
                duration = 500
                start()
            }
        }, 1800)
    }

    private fun animateBackgroundElements() {
        // Subtle floating animation for decorative circles
        val circle1Y = ObjectAnimator.ofFloat(circle1, "translationY", 0f, -20f, 0f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val circle2Y = ObjectAnimator.ofFloat(circle2, "translationY", 0f, 30f, 0f).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun animateTextElements() {
        // Brand name animation
        val brandFadeIn = ObjectAnimator.ofFloat(brandNameText, "alpha", 0f, 1f).apply {
            duration = 800
        }

        val brandSlideUp = ObjectAnimator.ofFloat(brandNameText, "translationY", 50f, 0f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }

        // Tagline animation
        val taglineFadeIn = ObjectAnimator.ofFloat(taglineText, "alpha", 0f, 1f).apply {
            duration = 800
            startDelay = 200
        }

        val taglineSlideUp = ObjectAnimator.ofFloat(taglineText, "translationY", 30f, 0f).apply {
            duration = 800
            startDelay = 200
            interpolator = DecelerateInterpolator()
        }

        // Play text animations together
        val textAnimSet = AnimatorSet().apply {
            playTogether(brandFadeIn, brandSlideUp, taglineFadeIn, taglineSlideUp)
            start()
        }
    }

    private fun animateProgressBar() {
        // Progress bar fade in
        val progressFadeIn = ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }

        // Progress animation
        progressBar.max = 100
        val progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100).apply {
            duration = 2200
            interpolator = DecelerateInterpolator()
            startDelay = 300
            start()
        }
    }
}