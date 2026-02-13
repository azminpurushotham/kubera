package com.collection.kubera.utils
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
class Converter {
    fun convertTimestampToString(timestamp: Timestamp): String {
        val date = timestamp.toDate() // Convert Firebase Timestamp to Date
        val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date) // Format the Date into a string
    }
}