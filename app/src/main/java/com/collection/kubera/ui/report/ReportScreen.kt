package com.collection.kubera.ui.report

import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.collection.kubera.R
import com.collection.kubera.states.ReportUiState
import com.collection.kubera.ui.theme.headingLabelD
import com.collection.kubera.ui.theme.labelD
import com.collection.kubera.ui.theme.primaryLightD
import com.collection.kubera.utils.dateFormate2
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: ReportViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState(ReportUiState.Initial)
    var isButtonEnabled by remember { mutableStateOf(false) }
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var message: String? = null
    var showDatePicker by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    val datePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val s =
                    LocalDate.ofEpochDay(utcTimeMillis / 86_400_000) // Convert millis to LocalDate
                return !s.isAfter(today) // ✅ Allow only past & today, disable future dates
            }
        }
    )
    var startDate: String? by remember { mutableStateOf(null) }
    var endDate: String? by remember {  mutableStateOf(null) }


    when (uiState) {
        ReportUiState.Initial -> {}
        ReportUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }

        }

        is ReportUiState.ReportCompleted -> {

        }

        is ReportUiState.ReportError -> {
            LaunchedEffect(Unit) {
                scope.launch {
                    message = (uiState as ReportUiState.ReportError).errorMessage
                    Timber.e(message)
                    snackbarHostState.showSnackbar(
                        message = message!!,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

        is ReportUiState.ReportInit -> {

        }

        is ReportUiState.ReportSuccess -> {
            Toast.makeText(
                context,
                (uiState as ReportUiState.ReportSuccess).message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            onDismissRequest = {
                startDate = datePickerState.selectedStartDateMillis?.let {
                    isButtonEnabled = true
                    dateFormate2.format(Date(it))
                }
                endDate = datePickerState.selectedEndDateMillis?.let {
                    dateFormate2.format(Date(it))
                }
                if (startDate != null && endDate == null) {
                    endDate = startDate
                }
                if (startDate == null && endDate != null) {
                    startDate = endDate
                }
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    startDate = datePickerState.selectedStartDateMillis?.let {
                        isButtonEnabled = true
                        dateFormate2.format(Date(it))
                    }
                    endDate = datePickerState.selectedEndDateMillis?.let {
                        dateFormate2.format(Date(it))
                    }
                    if (startDate != null && endDate == null) {
                        endDate = startDate
                    }
                    if (startDate == null && endDate != null) {
                        startDate = endDate
                    }
                    Timber.tag("DATE").i("$startDate - $endDate")
                    showDatePicker = false
                }) {
                    Text("Confirm", color = primaryLightD)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = primaryLightD)
                }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    todayDateBorderColor = primaryLightD,
                    todayContentColor = primaryLightD,
                    dayContentColor = MaterialTheme.colorScheme.onSurface,
                    disabledDayContentColor = MaterialTheme.colorScheme.onSecondary,
                    dateTextFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.error
                    )
                ),
            )
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center, // Vertically center items
        horizontalAlignment = Alignment.Start // Horizontally center items
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Generate Report",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight(300),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            "Collection report for a specific date range",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            color = labelD,
            modifier = Modifier.align(Alignment.Start),
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Green when enabled, Gray when disabled
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            onClick = {
                showDatePicker = true
            },
            content = {
                Text(
                    if (startDate != null && endDate != null) {
                        "$startDate - $endDate"
                    } else {
                        "Select Date Range"
                    },
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight(1),
                    color = headingLabelD
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                viewModel.generateReport(
                    "${path.absolutePath}/${
                        getString(
                            context,
                            R.string.app_name
                        )
                    }/Collection",
                    startDate!!,
                    endDate!!
                )
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled, // Control button's enabled state
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, // Green when enabled, Gray when disabled
                contentColor = if (isButtonEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                "Generate Report",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight(1),
                color = headingLabelD
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Today's Report",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight(300),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            "Collection report of today",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            color = labelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Button(
            onClick = {
                viewModel.todaysReport(
                    "${path.absolutePath}/${
                        getString(
                            context,
                            R.string.app_name
                        )
                    }/Collection"
                )
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Green when enabled, Gray when disabled
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                "Generate Today's Report",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight(1),
                color = headingLabelD
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Shops Report",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight(300),
            color = headingLabelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            "All shops current balance report",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight(1),
            color = labelD,
            modifier = Modifier.align(Alignment.Start)
        )
        Button(
            onClick = {
                viewModel.generateAllshops(
                    "${path.absolutePath}/${
                        getString(
                            context,
                            R.string.app_name
                        )
                    }/Shops"
                )
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Green when enabled, Gray when disabled
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                "All Shops",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight(1),
                color = headingLabelD
            )
        }
    }
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                contentColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.surface,
                content = {
                    Text(
                        text = message ?: ""
                    )
                }
            )
        }
    )

}

