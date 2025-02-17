package com.collection.kubera.utils
import timber.log.Timber
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date



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

@RequiresApi(Build.VERSION_CODES.O)
internal fun convertZonedDateTimeToTimestamp(zonedDateTime: ZonedDateTime): Timestamp {
    val instant = zonedDateTime.toInstant()
    return Timestamp(Date.from(instant))
}