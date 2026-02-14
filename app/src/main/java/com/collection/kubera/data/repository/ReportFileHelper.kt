package com.collection.kubera.data.repository

import android.content.Context
import com.collection.kubera.utils.deleteOldFile as deleteOldFileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ReportFileHelper {
    /**
     * Attempts to delete old file. Returns error message if deletion failed, null if successful.
     */
    fun deleteOldFile(fileName: String): String?
}

class ReportFileHelperImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ReportFileHelper {

    override fun deleteOldFile(fileName: String): String? =
        deleteOldFileUtil(context, fileName)
}
