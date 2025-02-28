package com.collection.kubera.utils
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import timber.log.Timber
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.google.firebase.Timestamp
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.reflect.KMutableProperty


val dmy  = "dd-MM-yyyy"
val dmyh  = "dd-MM-yyyy HH:mm:ss"
val dateFormate = SimpleDateFormat(dmyh)
val dateFormate2 = SimpleDateFormat(dmy)

@RequiresApi(Build.VERSION_CODES.O)
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


internal fun getDateFromTimestamp(timestamp: Long):  String {
    return dateFormate2.format(Date().apply {
        this.time = timestamp
    })
}

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
internal fun convertZonedDateTimeToTimestamp(zonedDateTime: ZonedDateTime): Timestamp {
    val instant = zonedDateTime.toInstant()
    return Timestamp(Date.from(instant))
}

inline fun <reified T> writeCsvFile(
    fileName: String,
    path: String,
    list: List<T>,
    schema: CsvSchema
) {
    var dir: File? = null
    dir = File(path)
    if (!dir.exists()) {
        dir.mkdirs()
    }

    val file = File(dir, fileName)
    if (file.exists()) {
        file.delete()
    }
    Timber.tag("isExist").i(file.exists().toString())

    val csvMapper = CsvMapper().apply {
        enable(CsvParser.Feature.TRIM_SPACES)
        enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    }
    csvMapper.writer().with(schema.withHeader()).writeValues(file).writeAll(list)
}

internal val permissionArray = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    )
} else {
null
}

//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal fun checkPermissions(
    applicationContext: Context,
    permissionArray: Array<String>?
): Boolean {
    var status = true
    permissionArray?.forEach {
        try {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                status = false
            }
        } catch (e: Exception) {
            Timber.e(e)
            status = false
        }
        Timber.i(it)
    }
    Timber.tag("checkPermissions").i(status.toString())
    return status
}

fun checkIfDirectoryExists(dir: String): Boolean {
    return File(dir).exists()
}