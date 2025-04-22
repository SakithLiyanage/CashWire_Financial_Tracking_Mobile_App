package com.example.cashwire.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cashwire.R

class OnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get the layout resource ID from arguments
        val layoutResId = arguments?.getInt(ARG_LAYOUT_RES_ID) ?: R.layout.fragment_onboarding_1
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add entrance animations for elements
        val imageOnboarding = view.findViewById<ImageView>(R.id.imageOnboarding)
        val titleText = view.findViewById<TextView>(R.id.titleText)
        val descriptionText = view.findViewById<TextView>(R.id.descriptionText)

        // Load animations
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        // Apply animations with delays
        imageOnboarding.startAnimation(fadeInAnimation)

        titleText.alpha = 0f
        titleText.postDelayed({
            titleText.alpha = 1f
            titleText.startAnimation(slideUpAnimation)
        }, 300)

        descriptionText.alpha = 0f
        descriptionText.postDelayed({
            descriptionText.alpha = 1f
            descriptionText.startAnimation(slideUpAnimation)
        }, 500)
    }

    companion object {
        private const val ARG_LAYOUT_RES_ID = "layout_res_id"

        fun newInstance(layoutResId: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            fragment.arguments = args
            return fragment
        }
    }
}