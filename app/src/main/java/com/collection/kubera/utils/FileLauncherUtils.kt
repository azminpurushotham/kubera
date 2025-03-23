package com.collection.kubera.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.collection.kubera.R
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import timber.log.Timber
import java.io.File

val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

fun isTreeUriPersisted(context: Context): Boolean {
    val uriPermissionList = context.contentResolver.persistedUriPermissions
    return uriPermissionList.isNotEmpty() && uriPermissionList[0].isReadPermission && uriPermissionList[0].isWritePermission
}

fun showDialogueForFileLauncher(
    context: Context,
    openDocumentTreeLauncher: ActivityResultLauncher<Intent>
) {
    AlertDialog.Builder(context)
        .apply {
            setTitle("Important")
            setMessage("Select a folder to store reports")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Close the dialog
                openDocumentTreeLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                })
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Close the dialog
            }
            create()
        }.show()
}

fun showDialogueForFileLauncher(
    context: Context,
    openDocumentTreeLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    AlertDialog.Builder(context)
        .apply {
            setTitle("Important")
            setMessage("Select a folder to store reports")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Close the dialog
                openDocumentTreeLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                })
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Close the dialog
            }
            create()
        }.show()
}

fun createDirectory(uri: Uri, context: Context) {
    try {
        val directoryName = context.getString(R.string.app_name)
        val newDirUri = DocumentsContract.createDocument(
            context.contentResolver,
            uri,
            DocumentsContract.Document.MIME_TYPE_DIR,
            directoryName
        )

        if (newDirUri != null) {
            Timber.v("DirectoryCreation", "Directory created: $directoryName")
        } else {
            Timber.e("DirectoryCreation", "Failed to create directory")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


inline fun <reified T> writeCsvFile(
    fileName: String,
    path: String,
    list: List<T>,
    schema: CsvSchema,
) {
    val dir = File(path)
    val file = File(dir, fileName)
    val csvMapper = CsvMapper().apply {
        enable(CsvParser.Feature.TRIM_SPACES)
        enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    }
    csvMapper.writer().with(schema.withHeader()).writeValues(file).writeAll(list)
}

fun deleteOldFile(context: Context, fileName: String) : String?{
    val uri = MediaStore.Files.getContentUri("external")
    val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
    val selectionArgs = arrayOf(fileName, Environment.DIRECTORY_DOCUMENTS + "/kashflowclt/Shops/")

    val deletedRows = context.contentResolver.delete(uri, selection, selectionArgs)

    if (deletedRows > 0) {
        Timber.tag("FileDelete").i("File deleted successfully")
        return "Please try again"
    } else {
        Timber.tag("FileDelete").i("Failed to delete file")
        return null
    }
}