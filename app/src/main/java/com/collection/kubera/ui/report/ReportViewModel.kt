package com.collection.kubera.ui.report
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.collection.kubera.data.BALANCE_COLLECTION
import com.collection.kubera.data.BalanceAmount
import com.collection.kubera.data.CollectionModel
import com.collection.kubera.data.SHOP_COLLECTION
import com.collection.kubera.data.Shop
import com.collection.kubera.data.TRANSECTION_HISTORY_COLLECTION
import com.collection.kubera.states.ReportUiState
import com.collection.kubera.utils.deleteOldFile
import com.collection.kubera.utils.getCurrentDate
import com.collection.kubera.utils.toEndTimestamp
import com.collection.kubera.utils.toTimestamp
import com.collection.kubera.utils.writeCsvFile
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ReportViewModel(private val application: Context) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> get() = _balance
    init {
        getBalance()
    }


    internal fun getBalance() {
        Timber.tag("getBalance").i("getBalance")
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection(BALANCE_COLLECTION)
                .get()
                .addOnSuccessListener {
                    val balanceAmounts = it.documents.mapNotNull { item ->
                        item.toObject(BalanceAmount::class.java)
                            ?.apply {
                                id = item.id
                            }
                    }
                    if (balanceAmounts.isNotEmpty() && balanceAmounts[0].balance > 0) {
                        Timber.tag("getBalance").i(it.toString())
                        _balance.value = balanceAmounts[0].balance
                    } else {
                        _balance.value = 0L
                    }
                }
                .addOnFailureListener {
                    Timber.e(it)
                    _balance.value = 0L
                    _uiState.value =
                        ReportUiState.ReportError(it.message ?: "Unable to show balance")
                }
        }
    }

    fun todaysReport(path: String, date: String = getCurrentDate()
    ) {
        Timber.i("todaysReport")
        _uiState.value = ReportUiState.Loading
        val dir = File(path)
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                _uiState.value = ReportUiState.ReportError("Directories not created ${dir.name}")
            }
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
                        try {
                            item.toObject(CollectionModel::class.java)
                                ?.apply {
                                    id = item.id
                                }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                    Timber.i(r.toString())
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


    fun generateReport(path: String, startDate: String, endDate: String) {
        Timber.i("generateReport")
        _uiState.value = ReportUiState.Loading
        val dir = File(path)
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                _uiState.value = ReportUiState.ReportError("Directories not created ${dir.name}")
            }
        }
        val schema = getReportSchema()
        val startTimestamp = startDate.toTimestamp()
        val endTimestamp = endDate.toEndTimestamp()
        Timber.tag("generateReportDATE").i("$startTimestamp - $endTimestamp")

        if(startTimestamp!=null && endTimestamp!=null){
            firestore.collection(TRANSECTION_HISTORY_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
                .whereLessThanOrEqualTo("timestamp", endTimestamp)
                .get()
                .addOnSuccessListener {
                    val r = it.documents.mapNotNull { item ->
                        item.toObject(CollectionModel::class.java)
                            ?.apply {
                                id = item.id
                            }
                    }
                    if(r.isNotEmpty()){
                        Timber.tag("generateReportSIZE").i(r.size.toString())
                        try {
                            writeCsvFile(
                                fileName = "$startDate-$endDate.csv",
                                dir.absolutePath,
                                list = r,
                                schema = schema
                            )
                            _uiState.value = ReportUiState.ReportSuccess("$startDate - $endDate report generated successfully")
                        } catch (e: Exception) {
                            _uiState.value = ReportUiState.ReportError("Failed to create report for $startDate - $endDate ERROR $e")
                        }
                    }else{
                        _uiState.value = ReportUiState.ReportError("No collection report for $startDate - $endDate")
                    }
                }
                .addOnFailureListener {e->
                    _uiState.value = ReportUiState.ReportError("No collection report for $startDate - $endDate $e")
                }
        }else{
            _uiState.value = ReportUiState.ReportError("Invalid date format")
        }

    }

    fun generateAllshops(path: String) {
        Timber.i("generateAllshops")
        _uiState.value = ReportUiState.Loading
        val dir = File(path)
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                _uiState.value = ReportUiState.ReportError("Directories not created ${dir.name}")
            }
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
                    }catch (e: FileNotFoundException) {
                        Timber.tag("FileNotFoundException").i("DELETE -> ${dir.delete()}")
                        if(!dir.delete()){
                            deleteOldFile(context = application,dir.name)?.let {
                                _uiState.value = ReportUiState.ReportError(it)
                            }?:run {
                                _uiState.value = ReportUiState.ReportError("Please DELETE old folder \"${dir.name}\" and try again")
                            }
                        }else{
                            _uiState.value = ReportUiState.ReportError("Please try again")
                        }
                    } catch (e: Exception) {
                        _uiState.value = ReportUiState.ReportError("Failed to create AllShops report ERROR $e \n\n Please DELETE old folder \"${dir.name}\" and try again")
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
            .addColumn("stability")
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
            .addColumn("stability")
            .build()
    }

    private val _uiState: MutableStateFlow<ReportUiState> =
        MutableStateFlow(ReportUiState.Initial)
    val uiState: MutableStateFlow<ReportUiState> = _uiState
}