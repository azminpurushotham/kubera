package com.collection.kubera.ui.report

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.collection.kubera.R
import com.collection.kubera.states.ReportUiState
import com.collection.kubera.ui.theme.headingLabelD
import com.collection.kubera.ui.theme.labelD
import com.collection.kubera.ui.theme.primaryLightD
import com.collection.kubera.utils.createDirectory
import com.collection.kubera.utils.dateFormate2
import com.collection.kubera.utils.isTreeUriPersisted
import com.collection.kubera.utils.path
import com.collection.kubera.utils.showDialogueForFileLauncher
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.time.LocalDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycleState by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val uiState by viewModel.uiState.collectAsState(ReportUiState.Initial)
    var isButtonEnabled by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    val datePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val s = LocalDate.ofEpochDay(utcTimeMillis / 86_400_000)
                return !s.isAfter(today)
            }
        }
    )
    var startDate: String? by remember { mutableStateOf(null) }
    var endDate: String? by remember { mutableStateOf(null) }

    val openDocumentTreeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Timber.tag("registerForActivityResult").d(result.toString())
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val takeFlags: Int = result.resultCode.and(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                createDirectory(uri, context)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ReportUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is ReportUiEvent.ShowSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (lifecycleState == Lifecycle.Event.ON_CREATE) {
        LaunchedEffect(Unit) {
            if (!isTreeUriPersisted(context)) {
                showDialogueForFileLauncher(context, openDocumentTreeLauncher)
            }
        }
    }

    when (uiState) {
        ReportUiState.Initial -> { /* Idle */ }
        ReportUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
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
                if (startDate != null && endDate == null) endDate = startDate
                if (startDate == null && endDate != null) startDate = endDate
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
                    if (startDate != null && endDate == null) endDate = startDate
                    if (startDate == null && endDate != null) startDate = endDate
                    Timber.tag("DATE").d("$startDate - $endDate")
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        BalanceHeader(viewModel)
        Spacer(modifier = Modifier.height(10.dp))
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            onClick = { showDatePicker = true },
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
                if (!isTreeUriPersisted(context)) {
                    showDialogueForFileLauncher(context, openDocumentTreeLauncher)
                } else {
                    viewModel.generateReport(
                        "${path.absolutePath}/${getString(context, R.string.app_name)}/Collection",
                        startDate!!,
                        endDate!!
                    )
                }
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
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
                if (!isTreeUriPersisted(context)) {
                    showDialogueForFileLauncher(context, openDocumentTreeLauncher)
                } else {
                    viewModel.todaysReport(
                        "${path.absolutePath}/${getString(context, R.string.app_name)}/Collection"
                    )
                }
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
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
                if (!isTreeUriPersisted(context)) {
                    showDialogueForFileLauncher(context, openDocumentTreeLauncher)
                } else {
                    viewModel.generateAllShops(
                        "${path.absolutePath}/${getString(context, R.string.app_name)}/Shops"
                    )
                }
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
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
                    Text(text = snackbarData.visuals.message)
                }
            )
        }
    )
}
