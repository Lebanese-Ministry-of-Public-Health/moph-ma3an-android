package com.tedmob.moph.tracer.onboarding

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.tedmob.moph.tracer.BuildConfig
import com.tedmob.moph.tracer.R
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.toolbar_default.*

class PrivacyWebViewActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        back.isVisible = true
        back.setOnClickListener { finish() }

        webview.loadUrl(BuildConfig.PRIVACY_URL)
    }
}
