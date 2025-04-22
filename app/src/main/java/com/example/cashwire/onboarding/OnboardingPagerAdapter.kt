package com.example.cashwire.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cashwire.R

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        OnboardingFragment.newInstance(R.layout.fragment_onboarding_1),
        OnboardingFragment.newInstance(R.layout.fragment_onboarding_2),
        OnboardingFragment.newInstance(R.layout.fragment_onboarding_3)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}