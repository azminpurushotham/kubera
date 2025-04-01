package com.collection.kubera.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Environment
import com.collection.kubera.R
import com.google.firebase.Timestamp
import fr.bipi.treessence.common.filters.NoFilter
import fr.bipi.treessence.common.formatter.LogcatFormatter
import fr.bipi.treessence.console.SystemLogTree
import fr.bipi.treessence.file.FileLoggerTree
import timber.log.Timber
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


val dmy = "dd-MM-yyyy"
val dmyh = "dd-MM-yyyy HH:mm:ss"
val dateFormate = SimpleDateFormat(dmyh)
val dateFormate2 = SimpleDateFormat(dmy)

var commonTimberDir: String? = null
var commonTimberTree: FileLoggerTree? = null
var timberDir: String? = null
var timberTree: FileLoggerTree? = null

internal fun getTodayStartAndEndTime(zoneId: ZoneId = ZoneId.systemDefault()): Pair<Timestamp, Timestamp> {
    // 1. Get Today's Date in the specified Time Zone
    val todayLocalDate = LocalDate.now(zoneId)

    // 2. Start of Day (00:00:00)
    val startOfDayLocalTime = LocalTime.MIN // 00:00:00
    val startOfDayZonedDateTime = ZonedDateTime.of(todayLocalDate, startOfDayLocalTime, zoneId)

    // 3. End of Day (23:59:59.999)
    val endOfDayLocalTime = LocalTime.MAX // 23:59:59.999999999
    val endOfDayZonedDateTime = ZonedDateTime.of(todayLocalDate, endOfDayLocalTime, zoneId)


    // 4. Convert to Firestore Timestamps
    val startTimestamp = convertZonedDateTimeToTimestamp(startOfDayZonedDateTime)
    val endTimestamp = convertZonedDateTimeToTimestamp(endOfDayZonedDateTime)

    Timber.tag("START").v(startOfDayZonedDateTime.toString())
    Timber.tag("END").v(endOfDayZonedDateTime.toString())

    return Pair(startTimestamp, endTimestamp)
}

internal fun getCurrentDateTime(): String {
    return dateFormate.format(Date())
}

internal fun getCurrentDate(): String {
    return dateFormate2.format(Date())
}


internal fun getDateFromTimestamp(timestamp: Long): String {
    return dateFormate2.format(Date().apply {
        this.time = timestamp
    })
}

fun String.toTimestamp(): Timestamp? {
    return try {
        val formatter = DateTimeFormatter.ofPattern(dmy)
        val localDate = LocalDate.parse(this, formatter)
        val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        Timestamp(Date.from(instant))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.toEndTimestamp(): Timestamp? {
    return try {
        val formatter = DateTimeFormatter.ofPattern(dmy)
        val localDate = LocalDate.parse(this, formatter)
        val endOfDay = localDate.atTime(LocalTime.MAX) // Max time of day
        val instant = endOfDay.atZone(ZoneId.systemDefault()).toInstant()
        Timestamp(Date.from(instant))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

internal fun convertZonedDateTimeToTimestamp(zonedDateTime: ZonedDateTime): Timestamp {
    val instant = zonedDateTime.toInstant()
    return Timestamp(Date.from(instant))
}

fun checkIfDirectoryExists(dir: String): Boolean {
    return File(dir).exists()
}

internal fun manualFileLogs(context: Context) {
    /* FOR LOGGING IN FILE*/
    var file = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/${context.getString(R.string.app_name)}/Logs/${getCurrentDate()}"
    Timber.tag("manualFileLogs").i(file)
    if (!checkIfDirectoryExists(file)
    ) {
        timberDir = null
        timberTree = null
    }

    if (timberDir == null) {
        file = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/${
            context.getString(R.string.app_name)
        }/Logs/${getCurrentDate()}/"
        Timber.tag("manualFileLogs").i(file)
        timberDir = file
    }

    if (timberTree == null) {
        timberTree =
            FileLoggerTree.Builder()
                .withFileName("${context.getString(R.string.app_name)}_log")
                .withDirName(timberDir!!)
                .withFilter(NoFilter.INSTANCE)
                .withFormatter(LogcatFormatter.INSTANCE)
                .withSizeLimit((1024 * 1024) * 3) // 3 MB
                .withFileLimit(100)
                .appendToFile(true)
                .build()
        Timber.plant(SystemLogTree())
        Timber.plant(Timber.DebugTree())
        Timber.plant(timberTree!!)
    }
}
