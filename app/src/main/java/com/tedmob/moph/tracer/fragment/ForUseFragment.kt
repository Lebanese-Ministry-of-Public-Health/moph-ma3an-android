package com.tedmob.moph.tracer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.R
import kotlinx.android.synthetic.main.fragment_upload_foruse.*

class ForUseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_foruse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forUseFragmentActionButton.setOnClickListener {
            (parentFragment as ForUseByOTCFragment).goToUploadFragment()
        }

        context?.let { context ->
            layoutVP.let {
                it.setPages(R.layout.fragment_upload_foruse_section_ar, R.layout.fragment_upload_foruse_section_en)
                it.setPickerSelection(Preference.getPreferredLanguage(context))
                it.onLanguageSelected = {
                    Preference.setPreferredLanguage(context, it)

                    when (it) {
                        0 -> {
                            forUseAboveButtonText.setText(R.string.step_1_text_2_ar)
                            forUseFragmentActionButton.setText(R.string.next_ar)
                        }
                        1 -> {
                            forUseAboveButtonText.setText(R.string.step_1_text_2_en)
                            forUseFragmentActionButton.setText(R.string.next)
                        }
                    }
                }
            }

            when (Preference.getPreferredLanguage(context)) {
                0 -> {
                    forUseAboveButtonText.setText(R.string.step_1_text_2_ar)
                    forUseFragmentActionButton.setText(R.string.next_ar)
                }
                1 -> {
                    forUseAboveButtonText.setText(R.string.step_1_text_2_en)
                    forUseFragmentActionButton.setText(R.string.next)
                }
            }
        }
    }
}
