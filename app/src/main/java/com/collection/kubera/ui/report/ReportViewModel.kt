package com.collection.kubera.ui.report
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.collection.kubera.data.CollectionHistory
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.states.ReportUiState
import com.collection.kubera.utils.getCurrentDate
import com.collection.kubera.utils.toTimestamp
import com.collection.kubera.utils.writeCsvFile
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ReportViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    fun todaysReport(path: String, date: String = getCurrentDate()
    ) {
        Timber.tag("todaysReport")
        _uiState.value = ReportUiState.Loading
        //            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val schema = getReportSchema()
        val localDateTime = LocalDateTime.now()
        val zoneId: ZoneId = ZoneId.systemDefault()
        val startOfDay = localDateTime.toLocalDate().atStartOfDay(zoneId)
        val endOfDay = localDateTime.toLocalDate().atTime(23, 59, 59).atZone(zoneId)

        val startTimestamp = Timestamp(Date.from(startOfDay.toInstant()))
        val endTimestamp = Timestamp(Date.from(endOfDay.toInstant()))

        firestore.collection(TRANSECTION_HISTORY_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
            .whereLessThanOrEqualTo("timestamp", endTimestamp)
            .get()
            .addOnSuccessListener {
                val r = it.documents.mapNotNull { item ->
                    item.toObject(CollectionHistory::class.java)
                        ?.apply {
                            id = item.id
                        }
                }
                if(r.isNotEmpty()){
                    try {
                        writeCsvFile(
                            fileName = "$date.csv",
                            dir.absolutePath,
                            list = r,
                            schema = schema
                        )
                        _uiState.value = ReportUiState.ReportSuccess("Today's $date report generated successfully")
                    } catch (e: Exception) {
                        _uiState.value = ReportUiState.ReportError("Failed to create today's report $date ERROR $e")
                    }
                }else{
                    _uiState.value = ReportUiState.ReportError("No collection report for today $date")
                }
            }
            .addOnFailureListener {e->
                _uiState.value = ReportUiState.ReportError("No collection report for today $date $e")
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun generateReport(path: String, startDate: String, endDate: String) {
        Timber.tag("generateReport")
        _uiState.value = ReportUiState.Loading
        //            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val schema = getReportSchema()
        val startTimestamp = startDate.toTimestamp()
        val endTimestamp = endDate.toTimestamp()

        if(startTimestamp!=null && endTimestamp!=null){
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
                .whereLessThanOrEqualTo("timestamp", endTimestamp)
                .get()
                .addOnSuccessListener {
                    val r = it.documents.mapNotNull { item ->
                        item.toObject(CollectionHistory::class.java)
                            ?.apply {
                                id = item.id
                            }
                    }
                    if(r.isNotEmpty()){
                        try {
                            writeCsvFile(
                                fileName = "$startDate-$endDate.csv",
                                dir.absolutePath,
                                list = r,
                                schema = schema
                            )
                            _uiState.value = ReportUiState.ReportSuccess("$startDate - $endDate report generated successfully")
                        } catch (e: Exception) {
                            _uiState.value = ReportUiState.ReportError("Failed to create today's report $startDate - $endDate ERROR $e")
                        }
                    }else{
                        _uiState.value = ReportUiState.ReportError("No collection report for today $startDate - $endDate")
                    }
                }
                .addOnFailureListener {e->
                    _uiState.value = ReportUiState.ReportError("No collection report for today $startDate - $endDate $e")
                }
        }else{
            _uiState.value = ReportUiState.ReportError("Invalid date format")
        }

    }

    fun generateAllshops(path: String) {
        _uiState.value = ReportUiState.Loading
        //            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val schema = getShopsSchema()
        firestore.collection(SHOP_COLLECTION)
            .get()
            .addOnSuccessListener {
                val r = it.documents.mapNotNull { item ->
                    item.toObject(Shop::class.java)
                        ?.apply {
                            id = item.id
                        }
                }
                if(r.isNotEmpty()){
                    try {
                        writeCsvFile(
                            fileName = "AllShops.csv",
                            dir.absolutePath,
                            list = r,
                            schema = schema
                        )
                        _uiState.value = ReportUiState.ReportSuccess("AllShops report generated successfully")
                    } catch (e: Exception) {
                        _uiState.value = ReportUiState.ReportError("Failed to create AllShops report ERROR $e")
                    }
                }else{
                    _uiState.value = ReportUiState.ReportError("Failed to create AllShops report")
                }
            }
            .addOnFailureListener {e->
                _uiState.value = ReportUiState.ReportError("Failed to create AllShops report $e")
            }
    }

    private fun getReportSchema(): CsvSchema {
        return CsvSchema.builder()
            .addColumn("Time")
            .addColumn("ShopName")
            .addColumn("FirstName")
            .addColumn("LastName")
            .addColumn("TransactionType")
            .addColumn("Amount")
            .addColumn("CollectedBy")
            .addColumn("Id")
            .addColumn("ShopId")
            .addColumn("ShopNameL")
            .addColumn("FirstNameL")
            .addColumn("LastNameL")
            .addColumn("PhoneNumber")
            .addColumn("SecondaryPhoneNumber")
            .addColumn("MailId")
            .addColumn("CollectedById")
            .build()
    }

    private fun getShopsSchema(): CsvSchema {
        return CsvSchema.builder()
            .addColumn("ShopName")
            .addColumn("FirstName")
            .addColumn("LastName")
            .addColumn("PhoneNumber")
            .addColumn("Balance")
            .addColumn("TransactionType")
            .addColumn("Time")
            .addColumn("Location")
            .addColumn("Landmark")
            .addColumn("Id")
            .addColumn("ShopNameL")
            .addColumn("FirstNameL")
            .addColumn("LastNameL")
            .addColumn("SecondaryPhoneNumber")
            .addColumn("MailId")
            .addColumn("Status")
            .build()
    }



    private val _uiState: MutableStateFlow<ReportUiState> =
        MutableStateFlow(ReportUiState.Initial)
    val uiState: MutableStateFlow<ReportUiState> = _uiState
}