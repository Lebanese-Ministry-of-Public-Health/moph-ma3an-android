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
import com.tedmob.moph.tracer.status.persistence.StatusRecord
import com.tedmob.moph.tracer.streetpass.persistence.StreetPassRecord
import kotlinx.android.synthetic.main.fragment_upload_page.*


data class ExportData(val recordList: List<StreetPassRecord>, val statusList: List<StatusRecord>)

class UploadPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val childFragMan: FragmentManager = childFragmentManager
        val childFragTrans: FragmentTransaction = childFragMan.beginTransaction()
        val fragB = VerifyCallerFragment()
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

    fun turnOnLoadingProgress() {
        uploadPageFragmentLoadingProgressBarFrame.visibility = View.VISIBLE
    }

    fun turnOffLoadingProgress() {
        uploadPageFragmentLoadingProgressBarFrame.visibility = View.INVISIBLE
    }

    fun navigateToUploadPin() {
        val childFragMan: FragmentManager = childFragmentManager
        val childFragTrans: FragmentTransaction = childFragMan.beginTransaction()
        val fragB = EnterPinFragment()
        //childFragTrans.add(R.id.fragment_placeholder, fragB)//fixme bug with add that prevents drawing top corners
        childFragTrans.replace(R.id.fragment_placeholder, fragB)
        childFragTrans.addToBackStack("C")
        childFragTrans.commit()
    }

    fun goBackToHome() {
        (activity as MainActivity).goToHome()
    }

    fun navigateToUploadComplete() {
        val childFragMan: FragmentManager = childFragmentManager
        val childFragTrans: FragmentTransaction = childFragMan.beginTransaction()
        val fragB = UploadCompleteFragment()
        //childFragTrans.add(R.id.fragment_placeholder, fragB)//fixme bug with add that prevents drawing top corners
        childFragTrans.replace(R.id.fragment_placeholder, fragB)
        childFragTrans.addToBackStack("UploadComplete")
        childFragTrans.commit()
    }

    fun popStack() {
        childFragmentManager.popBackStack()
    }
}
