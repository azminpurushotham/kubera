package com.collection.kubera.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.collection.kubera.R
import com.collection.kubera.ui.AppNavGraph
import com.collection.kubera.ui.theme.KuberaTheme
import com.collection.kubera.utils.checkIfDirectoryExists
import com.collection.kubera.utils.checkPermissions
import com.collection.kubera.utils.permissionArray
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KuberaTheme {
                AppNavGraph()
            }
        }

        permissionArray?.let {
            requestPermission()
        }?:run {
            launchFileChooser()
        }
    }


    private var openDocumentTreeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Timber.tag("registerForActivityResult").i(result.toString())
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val takeFlags: Int = result.data?.flags?.and(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                ) ?: 0
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                createDirectory(uri)
            }
        }
    }

    // on below line we are creating a function to request permission.
    private fun requestPermission() {
        Timber.tag("requestPermission").i(permissionArray?.toString())
        permissionArray?.let {
            requestPermissionLauncher.launch(it)
        }?:run {
            launchFileChooser()
        }
    }

    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // on below line we are checking if result size is > 0
        if (permissions.entries.isNotEmpty()) {
            // on below line we are checking
            // if both the permissions are granted.
            permissions.entries.all { it.value }.also {
                if (it) {
                    launchFileChooser()
                } else {
                    val dp = permissions.toList()
                        .filter { (s, i) -> !i }
                        .map { (s, i) -> s }
                    Timber.tag("Denied Permissions").e(dp.toString())
                    Toast.makeText(this, "Permission Denied For ${dp}", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }

    private fun launchFileChooser() {
        if (checkPermissions(this, permissionArray)) {
            Timber.tag("onStart").i("onStart")
            if (checkIfDirectoryExists(
                    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/${
                        ContextCompat.getString(this, R.string.app_name)
                    }"
                )
            ) {
                Timber.tag("launchFileChooser").i("FILE EXISTS")
            } else {
                // Register the launcher and define the callback
                Timber.tag("launchFileChooser").i("FIle permission launcher")
                showDialogueForFileLauncher()
            }
        } else {
            requestPermission()
        }
    }

    private fun showDialogueForFileLauncher() {
        AlertDialog.Builder(this@MainActivity)
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

    private fun createDirectory(uri: Uri) {
        try {
            val directoryName = getString(R.string.app_name)
            val newDirUri = DocumentsContract.createDocument(
                contentResolver,
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
}