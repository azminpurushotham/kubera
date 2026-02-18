package com.collection.kubera.domain.repository

interface ReportFileHelper {
    /**
     * Attempts to delete old file. Returns error message if deletion failed, null if successful.
     */
    fun deleteOldFile(fileName: String): String?
}
