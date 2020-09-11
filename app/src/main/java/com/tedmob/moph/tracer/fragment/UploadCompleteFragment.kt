package com.tedmob.moph.tracer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.R
import kotlinx.android.synthetic.main.fragment_upload_uploadcomplete.*

class UploadCompleteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_uploadcomplete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->
            layoutVP.let {
                it.setPages(
                    R.layout.fragment_upload_uploadcomplete_section_ar,
                    R.layout.fragment_upload_uploadcomplete_section_en
                )
                it.setPickerSelection(Preference.getPreferredLanguage(context))
                it.onLanguageSelected = { Preference.setPreferredLanguage(context, it) }
            }
        }

        uploadCompleteFragmentActionButton.setOnClickListener {
            (parentFragment as UploadPageFragment).goBackToHome()
        }

        uploadCompleteBackButton.setOnClickListener {
            (parentFragment as UploadPageFragment).goBackToHome()
        }
    }
}
