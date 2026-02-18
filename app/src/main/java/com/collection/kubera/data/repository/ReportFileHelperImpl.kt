package com.collection.kubera.data.repository

import android.content.Context
import com.collection.kubera.domain.repository.ReportFileHelper
import com.collection.kubera.utils.deleteOldFile as deleteOldFileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReportFileHelperImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ReportFileHelper {

    override fun deleteOldFile(fileName: String): String? =
        deleteOldFileUtil(context, fileName)
}
