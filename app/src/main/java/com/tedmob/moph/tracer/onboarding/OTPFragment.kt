package com.tedmob.moph.tracer.onboarding

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.R
import com.tedmob.moph.tracer.Utils
import com.tedmob.moph.tracer.logging.CentralLog
import kotlinx.android.synthetic.main.fragment_otp.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlin.math.floor

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OTPFragment : OnboardingFragmentInterface() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val TAG: String = "OTPFragment"
    private val COUNTDOWN_DURATION = 2 * 60L
    private var stopWatch: CountDownTimer? = null

    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            startTimer()
        } else {
            resetTimer()
        }
    }

    private fun resetTimer() {
        stopWatch?.cancel()
    }

    override fun getButtonText(): String = context?.let {
        if (Preference.getPreferredLanguage(it) == 1) getString(R.string.verify) else getString(R.string.verify_ar)
    } ?: getString(R.string.verify_ar)

    override fun becomesVisible() {}

    override fun onButtonClick(view: View) {
        CentralLog.d(TAG, "OnButtonClick 3B")

        val otp = getOtp()
        val onboardActivity = context as OnboardingActivity
        onboardActivity.validateOTP(otp)
    }

    override fun getProgressValue(): Int = 40

    private fun getOtp(): String = pinView.pin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.isVisible = true
        back.setOnClickListener { (activity as? OnboardingActivity?)?.navigateToPreviousPage() }

        resendCode.setOnClickListener {
            if (phoneNumber != null) {
                CentralLog.d(TAG, "resend pressed")
                val onboardingActivity = activity as OnboardingActivity
                onboardingActivity.resendCode(phoneNumber!!)
                resetTimer()
                startTimer()
            }
        }

        pinView.onTextChanged = { s, start, before, count ->
            if (s.length == 6) {
                Utils.hideKeyboardFrom(view.context, view)
            }
        }

        pinView.onEditorActionListener = { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                Utils.hideKeyboardFrom(view.context, view)
                val otp = getOtp()
                val onboardActivity = context as OnboardingActivity
                onboardActivity.validateOTP(otp)
                true
            } else {
                false
            }
        }
    }

    override fun onUpdatePhoneNumber(num: String) {
        CentralLog.d(TAG, "onUpdatePhoneNumber $num")
        phoneNumber = num
    }

    private fun startTimer() {
        stopWatch = object : CountDownTimer(COUNTDOWN_DURATION * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val numberOfMins = floor((millisUntilFinished * 1.0) / 60000)
                val numberOfMinsInt = numberOfMins.toInt()
                val numberOfSeconds = floor((millisUntilFinished / 1000.0) % 60)
                val numberOfSecondsInt = numberOfSeconds.toInt()

                expiryText?.text = getString(R.string.code_expiry_message_format, numberOfMinsInt, numberOfSecondsInt)
            }

            override fun onFinish() {
                expiryText?.text = "00:00"
                resendCode.isEnabled = true
                resendCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.greenMain))
            }
        }
        stopWatch?.start()
        resendCode.isEnabled = false
        resendCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyBgd))
    }

    override fun onError(error: String) {
        try {
            AlertDialog.Builder(requireActivity())
                .setMessage(error)
                .setPositiveButton(R.string.ok, null)
                .show()
        } catch (e: Exception) {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null

    }

    override fun onDestroy() {
        super.onDestroy()
        stopWatch?.cancel()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
