package com.tedmob.moph.tracer.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.R
import kotlinx.android.synthetic.main.main_activity_onboarding.*

class PreOnboardingActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_onboarding)

        next.setOnClickListener {
            startActivity(Intent(this, HowItWorksActivity::class.java))
        }

        layoutVP.let {
            it.setPages(R.layout.main_activity_onboarding_section_ar, R.layout.main_activity_onboarding_section_en)
            it.setPickerSelection(Preference.getPreferredLanguage(this))
            it.onLanguageSelected = {
                Preference.setPreferredLanguage(this, it)

                when (Preference.getPreferredLanguage(this)) {
                    0 -> {
                        next.setText(R.string.agree_ar)
                        header0En.setText(R.string.consent_1_header_0_ar)
                    }
                    1 -> {
                        next.setText(R.string.agree)
                        header0En.setText(R.string.consent_1_header_0_en)
                    }
                }
            }

            when (Preference.getPreferredLanguage(this)) {
                0 -> {
                    next.setText(R.string.agree_ar)
                    header0En.setText(R.string.consent_1_header_0_ar)
                }
                1 -> {
                    next.setText(R.string.agree)
                    header0En.setText(R.string.consent_1_header_0_en)
                }
            }
        }
    }
}
