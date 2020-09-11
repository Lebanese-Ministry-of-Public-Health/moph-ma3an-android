package com.tedmob.moph.tracer.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.tedmob.modules.textstyler.setStyledText
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.R
import com.tedmob.moph.tracer.logging.CentralLog
import kotlinx.android.synthetic.main.fragment_tou.*
import kotlinx.android.synthetic.main.toolbar_transparent.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TOUFragment : OnboardingFragmentInterface() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val TAG: String = "TOUFragment"
    private lateinit var mainContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getButtonText(): String = context?.let {
        if (Preference.getPreferredLanguage(it) == 1) getString(R.string.submit) else getString(R.string.submit_ar)
    } ?: getString(R.string.submit_ar)

    override fun onUpdatePhoneNumber(num: String) {}

    override fun onError(error: String) {}

    override fun becomesVisible() {}

    override fun onButtonClick(buttonView: View) {
        CentralLog.d(TAG, "OnButtonClick 4")
        (context as OnboardingActivity).navigateToNextPage()
    }

    override fun getProgressValue(): Int = 60

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tou, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.isVisible = true
        back.setOnClickListener { (activity as? OnboardingActivity?)?.navigateToPreviousPage() }

        context?.let { context ->
            layoutVP.let {
                it.setPages(R.layout.fragment_tou_section_ar, R.layout.fragment_tou_section_en)
                it.setPickerSelection(Preference.getPreferredLanguage(context))
                it.onLanguageSelected = {
                    Preference.setPreferredLanguage(context, it)
                    updateButtonText()

                    when (it) {
                        0 -> {
                            privacyEn.setStyledText {
                                section("${getString(R.string.read_our_ar)} ")
                                section(getString(R.string.privacy_safeguards_ar)) {
                                    onClick { startActivity(Intent(mainContext, PrivacyWebViewActivity::class.java)) }
                                }
                            }
                        }
                        1 -> {
                            privacyEn.setStyledText {
                                section("${getString(R.string.read_our)} ")
                                section(getString(R.string.privacy_safeguards)) {
                                    onClick { startActivity(Intent(mainContext, PrivacyWebViewActivity::class.java)) }
                                }
                            }
                        }
                    }
                }
            }

            when (Preference.getPreferredLanguage(context)) {
                0 -> {
                    privacyEn.setStyledText {
                        section("${getString(R.string.read_our_ar)} ")
                        section(getString(R.string.privacy_safeguards_ar)) {
                            onClick { startActivity(Intent(mainContext, PrivacyWebViewActivity::class.java)) }
                        }
                    }
                }
                1 -> {
                    privacyEn.setStyledText {
                        section("${getString(R.string.read_our)} ")
                        section(getString(R.string.privacy_safeguards)) {
                            onClick { startActivity(Intent(mainContext, PrivacyWebViewActivity::class.java)) }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainContext = context
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
