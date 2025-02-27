package com.collection.kubera.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.collection.kubera.utils.dateFormate
import com.collection.kubera.utils.dmyh
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object DateConverter {
    @TypeConverter
    fun toDate(timestamp: Timestamp?): String? {
        return timestamp?.let { dateFormate.format(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toTimestamp(date: String?): Timestamp? {
        return try {
            val formatter = DateTimeFormatter.ofPattern(dmyh)
            val localDateTime = LocalDateTime.parse(date, formatter)
            val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
            Timestamp(Date.from(instant))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun serverTimestamp(): FieldValue {
        return FieldValue.serverTimestamp()
    }
}
