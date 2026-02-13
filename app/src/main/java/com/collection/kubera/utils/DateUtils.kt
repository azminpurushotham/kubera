package com.collection.kubera.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun formatFirestoreTimestamp(timestamp: Timestamp?): String? {
    if (timestamp == null) return null
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}
