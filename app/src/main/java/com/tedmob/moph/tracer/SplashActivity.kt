package com.tedmob.moph.tracer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.BidiFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tedmob.moph.tracer.onboarding.PreOnboardingActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME: Long = 1500
    private var needToUpdateApp = false

    private val mHandler: Handler by lazy { Handler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val bidi = BidiFormatter.getInstance()
        version.text = String.format(getString(R.string.version_name), bidi.unicodeWrap(BuildConfig.VERSION_NAME))

        //check if the intent was from notification and its a update notification
        /*intent.extras?.let {
            val notifEvent: String? = it.getString("event", null)

            notifEvent?.let {
                if (it.equals("update")) {
                    needToUpdateApp = true

                    startActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(BuildConfig.STORE_URL)
                        }
                    )
                    finish()
                }
            }
        }*/
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        if (!needToUpdateApp) {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                //goToNextScreen()
                if (Preference.getPreferredLanguage(this) == -1) {
                    showLanguages()
                } else {
                    goToNextScreen()
                }
            }, SPLASH_TIME)
        }
    }

    private fun showLanguages() {
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, R.layout.spinner_dropdown_item_language, languages)
        try {
            MaterialAlertDialogBuilder(this)
                .setAdapter(adapter) { _, which ->
                    Preference.setPreferredLanguage(this, which)
                    goToNextScreen()
                }
                .setCancelable(false)
                .show()
        } catch (e: WindowManager.BadTokenException) {
        }
    }

    private fun goToNextScreen() {
        if (!Preference.isOnBoarded(this)) {
            startActivity(Intent(this, PreOnboardingActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
