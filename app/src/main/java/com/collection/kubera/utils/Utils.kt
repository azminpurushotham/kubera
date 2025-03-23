package com.collection.kubera.utils

import android.icu.text.SimpleDateFormat
import com.google.firebase.Timestamp
import timber.log.Timber
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
