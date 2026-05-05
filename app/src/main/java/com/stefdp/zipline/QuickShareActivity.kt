package com.stefdp.zipline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.transferservice.TransferService
import com.stefdp.zipline.utils.copyUriToTempFile
import com.stefdp.zipline.utils.getFileInfo
class QuickShareActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        finish()
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_SEND && action != Intent.ACTION_SEND_MULTIPLE) return

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        val uris = if (action == Intent.ACTION_SEND) {
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)?.let { listOf(it) }
        } else {
            IntentCompat.getParcelableArrayListExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
        }

        if (!uris.isNullOrEmpty()) {
            Toast.makeText(this, "Uploading ${uris.size} file(s)...", Toast.LENGTH_SHORT).show()

            val tempFilePaths = uris.mapNotNull { uri ->
                val displayName = getFileInfo(applicationContext, uri)?.first ?: return@mapNotNull null
                copyUriToTempFile(applicationContext, uri, displayName)?.absolutePath
            }

            if (tempFilePaths.isNotEmpty()) {
                val serviceIntent = Intent(this, TransferService::class.java).apply {
                    this.action = TransferService.ACTION_QUICK_SHARE
                    putStringArrayListExtra(TransferService.EXTRA_FILE_PATHS, ArrayList(tempFilePaths))
                }
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        } else if (text != null && text.startsWith("http")) {
            Toast.makeText(this, "Shortening URL...", Toast.LENGTH_SHORT).show()

            val serviceIntent = Intent(this, TransferService::class.java).apply {
                this.action = TransferService.ACTION_QUICK_SHARE
                putExtra(TransferService.EXTRA_TEXT, text)
            }
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        finish()
    }
}