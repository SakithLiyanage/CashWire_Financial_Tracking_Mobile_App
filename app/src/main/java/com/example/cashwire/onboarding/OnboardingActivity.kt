package com.example.cashwire.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.cashwire.R
import com.example.cashwire.auth.WelcomeActivity
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: MaterialButton
    private lateinit var btnSkip: Button
    private lateinit var indicators: Array<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Initialize views
        viewPager = findViewById(R.id.onboardingViewPager)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)

        // Set up indicators
        indicators = arrayOf(
            findViewById(R.id.indicator1),
            findViewById(R.id.indicator2),
            findViewById(R.id.indicator3)
        )

        // Set up the ViewPager2 with adapter
        viewPager.adapter = OnboardingPagerAdapter(this)

        // Disable swiping animation to use our custom animations
        viewPager.setPageTransformer { page, position ->
            when {
                position < -1 || position > 1 -> {
                    page.alpha = 0f
                }
                position <= 0 -> {
                    page.alpha = 1 + position
                    page.translationX = page.width * -position
                }
                position <= 1 -> {
                    page.alpha = 1 - position
                    page.translationX = page.width * -position
                }
            }
        }

        // Handle ViewPager2 page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)

                // Change button text on last page
                if (position == indicators.size - 1) {
                    btnNext.text = "GET STARTED"
                } else {
                    btnNext.text = "NEXT"
                }

                // Add button animation
                btnNext.startAnimation(AnimationUtils.loadAnimation(
                    this@OnboardingActivity, R.anim.pulse))
            }
        })

        // Set up button click listeners
        btnNext.setOnClickListener {
            if (viewPager.currentItem == indicators.size - 1) {
                // On the last page, go to the main app
                finishOnboarding()
            } else {
                // Otherwise, go to next page
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }

        btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updateIndicators(position: Int) {
        for (i in indicators.indices) {
            indicators[i].background = if (i == position)
                ContextCompat.getDrawable(this, R.drawable.indicator_active)
            else
                ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
        }
    }

    private fun finishOnboarding() {
        // Save a flag that onboarding is complete
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("onboarding_complete", true).apply()

        // Navigate to dashboard
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()

        // Add sliding animation for transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}