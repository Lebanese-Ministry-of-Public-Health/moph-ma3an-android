package com.tedmob.moph.tracer.fragment

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.tedmob.moph.tracer.*
import com.tedmob.moph.tracer.logging.CentralLog
import com.tedmob.moph.tracer.status.persistence.StatusRecord
import com.tedmob.moph.tracer.status.persistence.StatusRecordStorage
import com.tedmob.moph.tracer.streetpass.persistence.StreetPassRecord
import com.tedmob.moph.tracer.streetpass.persistence.StreetPassRecordStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_upload_enterpin.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class EnterPinFragment : Fragment() {
    private var TAG = "UploadFragment"

    private var disposeObj: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_enterpin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->
            layoutVP2.let {
                it.showPicker(false)
                it.setPages(
                    R.layout.fragment_upload_enterpin_section_2_ar,
                    R.layout.fragment_upload_enterpin_section_2_en
                )
                it.setPickerSelection(Preference.getPreferredLanguage(context))
                it.onLanguageSelected = {
                    Preference.setPreferredLanguage(context, it)
                    layoutVP1.setPickerSelection(it)
                }
            }

            layoutVP1.let {
                it.showPageIndicator(false)
                it.setPages(
                    R.layout.fragment_upload_enterpin_section_1_ar,
                    R.layout.fragment_upload_enterpin_section_1_en
                )
                it.setPickerSelection(Preference.getPreferredLanguage(context))
                it.onLanguageSelected = {
                    Preference.setPreferredLanguage(context, it)
                    layoutVP2.setPickerSelection(it)
                }
            }
        }

        enterPinFragmentUploadCode.onTextChanged = { s, start, before, count ->
            if (s.length == 6) {
                Utils.hideKeyboardFrom(view.context, view)
            }
        }

        enterPinActionButton.setOnClickListener {
            enterPinFragmentErrorMessage.visibility = View.INVISIBLE
            (parentFragment as UploadPageFragment).turnOnLoadingProgress()

            val observableStreetRecords = Observable.create<List<StreetPassRecord>> {
                val result = StreetPassRecordStorage(TracerApp.AppContext).getAllRecords()
                it.onNext(result)
            }
            val observableStatusRecords = Observable.create<List<StatusRecord>> {
                val result = StatusRecordStorage(TracerApp.AppContext).getAllRecords()
                it.onNext(result)
            }

            disposeObj = Observable.zip(
                observableStreetRecords,
                observableStatusRecords,
                BiFunction<List<StreetPassRecord>, List<StatusRecord>, ExportData> { records, status ->
                    ExportData(records, status)
                }
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { exportedData ->
                    Log.d(TAG, "records: ${exportedData.recordList}")
                    Log.d(TAG, "status: ${exportedData.statusList}")

                    getUploadToken(enterPinFragmentUploadCode.pin)
                        .addOnSuccessListener {
                            val response = it.data as HashMap<String, String>
                            try {
                                val uploadToken = response["token"]
                                CentralLog.d(TAG, "uploadToken: $uploadToken")

                                val task = writeToInternalStorageAndUpload(
                                    TracerApp.AppContext,
                                    exportedData.recordList,
                                    exportedData.statusList,
                                    uploadToken
                                )
                                task.addOnFailureListener {
                                    CentralLog.d(TAG, "failed to upload")

                                    (parentFragment as UploadPageFragment).turnOffLoadingProgress()
                                    enterPinFragmentErrorMessage.visibility = View.VISIBLE
                                }.addOnSuccessListener {
                                    CentralLog.d(TAG, "uploaded successfully")

                                    (parentFragment as UploadPageFragment).run {
                                        turnOffLoadingProgress()
                                        navigateToUploadComplete()
                                    }
                                }
                            } catch (e: Throwable) {
                                CentralLog.d(TAG, "Failed to upload data: ${e.message}")

                                (parentFragment as UploadPageFragment).turnOffLoadingProgress()
                                enterPinFragmentErrorMessage.visibility = View.VISIBLE
                            }
                        }.addOnFailureListener {
                            CentralLog.d(TAG, "Invalid code")

                            (parentFragment as? UploadPageFragment)?.turnOffLoadingProgress()
                            enterPinFragmentErrorMessage.visibility = View.VISIBLE
                        }
                }
        }

        enterPinFragmentBackButton.setOnClickListener {
            (parentFragment as UploadPageFragment).popStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeObj?.dispose()
    }

    private fun getUploadToken(uploadCode: String): Task<HttpsCallableResult> {
        val functions = FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION)
        return functions
            .getHttpsCallable("getUploadToken")
            .call(uploadCode)
    }

    private fun writeToInternalStorageAndUpload(
        context: Context,
        deviceDataList: List<StreetPassRecord>,
        statusList: List<StatusRecord>,
        uploadToken: String?
    ): UploadTask {
        val date = Utils.getDateFromUnix(System.currentTimeMillis())
        val gson = Gson()

        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL

        val updatedDeviceList = deviceDataList.map {
            it.timestamp = it.timestamp / 1000
            return@map it
        }
        val updatedStatusList = statusList.map {
            it.timestamp = it.timestamp / 1000
            return@map it
        }

        val map: MutableMap<String, Any> = hashMapOf(
            "token" to uploadToken as Any,
            "records" to updatedDeviceList as Any,
            "events" to updatedStatusList as Any
        )
        val mapString = gson.toJson(map)

        val fileName = "StreetPassRecord_${manufacturer}_${model}_$date.json"
        val fileOutputStream: FileOutputStream

        val uploadDir = File(context.filesDir, "upload")

        if (uploadDir.exists()) {
            uploadDir.deleteRecursively()
        }

        uploadDir.mkdirs()
        val fileToUpload = File(uploadDir, fileName)
//        fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fileOutputStream = FileOutputStream(fileToUpload)

        fileOutputStream.write(mapString.toByteArray())
        fileOutputStream.close()

        return uploadToCloudStorage(context, fileToUpload)
    }

    private fun uploadToCloudStorage(context: Context, fileToUpload: File): UploadTask {
        CentralLog.d(TAG, "Uploading to Cloud Storage")

        val bucketName = BuildConfig.FIREBASE_UPLOAD_BUCKET
        val storage = FirebaseStorage.getInstance("gs://${bucketName}")
        val storageRef = storage.getReferenceFromUrl("gs://${bucketName}")

        val dateString = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date())
        val streetPassRecordsRef =
            storageRef.child("streetPassRecords/$dateString/${fileToUpload.name}")

        val fileUri: Uri =
            FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                fileToUpload
            )

        val uploadTask = streetPassRecordsRef.putFile(fileUri)
        uploadTask.addOnCompleteListener {
            try {
                fileToUpload.delete()
                CentralLog.i(TAG, "upload file deleted")
            } catch (e: Exception) {
                CentralLog.e(TAG, "Failed to delete upload file")
            }
        }
        return uploadTask
    }

}
