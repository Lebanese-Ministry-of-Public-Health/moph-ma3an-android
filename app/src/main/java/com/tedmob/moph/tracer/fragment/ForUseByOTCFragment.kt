package com.tedmob.moph.tracer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.tedmob.moph.tracer.MainActivity
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.R
import kotlinx.android.synthetic.main.fragment_forusebyotc.*

class ForUseByOTCFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forusebyotc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val childFragMan: FragmentManager = childFragmentManager
        val childFragTrans: FragmentTransaction = childFragMan.beginTransaction()
        val fragB = ForUseFragment()
        childFragTrans.add(R.id.fragment_placeholder, fragB)
        childFragTrans.addToBackStack("VerifyCaller")
        childFragTrans.commit()

        context?.let { context ->
            layoutVP.let {
                it.setPages(R.layout.fragment_foruserbyotc_header_ar, R.layout.fragment_foruserbyotc_header_en)
                it.setPickerSelection(Preference.getPreferredLanguage(context))
                it.onLanguageSelected = { Preference.setPreferredLanguage(context, it) }
                it.showPicker(false)
            }
        }
    }

    fun goToUploadFragment() {
        val parentActivity: MainActivity = activity as MainActivity
        parentActivity.openFragment(
            parentActivity.LAYOUT_MAIN_ID,
            UploadPageFragment(),
            UploadPageFragment::class.java.name,
            0
        )
    }
}
