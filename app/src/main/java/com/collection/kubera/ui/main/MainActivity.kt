package com.collection.kubera.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.collection.kubera.ui.AppNavGraph
import com.collection.kubera.ui.theme.KuberaTheme
import com.collection.kubera.utils.createDirectory
import com.collection.kubera.utils.isTreeUriPersisted
import com.collection.kubera.utils.showDialogueForFileLauncher
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KuberaTheme {
                AppNavGraph()
            }
        }

        val openDocumentTreeLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Timber.tag("registerForActivityResult").i(result.toString())
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val takeFlags: Int = result.resultCode.and(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                    createDirectory(uri,this@MainActivity)
                }
            }
        }

        if (!isTreeUriPersisted(this@MainActivity)) {
            showDialogueForFileLauncher(this@MainActivity,openDocumentTreeLauncher)
        }
    }
}