package com.tedmob.moph.tracer.streetpass.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.tedmob.moph.tracer.streetpass.persistence.StreetPassRecord
import com.tedmob.moph.tracer.streetpass.persistence.StreetPassRecordDatabase
import com.tedmob.moph.tracer.streetpass.persistence.StreetPassRecordRepository

class RecordViewModel(app: Application) : AndroidViewModel(app) {

    private var repo: StreetPassRecordRepository

    var allRecords: LiveData<List<StreetPassRecord>>

    init {
        val recordDao = StreetPassRecordDatabase.getDatabase(app).recordDao()
        repo = StreetPassRecordRepository(recordDao)
        allRecords = repo.allRecords
    }
}
