package com.stefdp.zipline

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.network.requests.createUrl
import com.stefdp.zipline.network.requests.getWebServerSettings
import com.stefdp.zipline.network.requests.uploadFile
import com.stefdp.zipline.network.requests.uploadPartialFile
import com.stefdp.zipline.transferservice.TransferService
import com.stefdp.zipline.utils.copyUriToTempFile
import com.stefdp.zipline.utils.getFileInfo
import com.stefdp.zipline.utils.parseBytes
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickShareActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        super.onCreate(savedInstanceState)

        handleIntent(intent)

        moveTaskToBack(true)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action ?: finish()

        if (action != Intent.ACTION_SEND && action != Intent.ACTION_SEND_MULTIPLE) {
            finish()
        }

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text != null && (text.startsWith(
            prefix = "http://",
            ignoreCase = true
        ) || text.startsWith(
            prefix = "https://",
            ignoreCase = true))
        ) {
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch(Dispatchers.IO) {
                createUrl(
                    context = applicationContext,
                    destination = text
                )
                    .onSuccess {
                        copyToClipboardAndNotify(it.url)
                    }.onFailure { error ->
                        showToast("Shorten Failed: ${error.message}")
                    }

                finish()
            }
            return
        }

        val uris = if (action == Intent.ACTION_SEND) {
            IntentCompat.getParcelableExtra(
                intent,
                Intent.EXTRA_STREAM,
                Uri::class.java
            )?.let { listOf(it) }
        } else {
            IntentCompat.getParcelableArrayListExtra(
                intent,
                Intent.EXTRA_STREAM,
                Uri::class.java
            )
        }

        if (!uris.isNullOrEmpty()) {
            processFileUploads(uris)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun processFileUploads(uris: List<Uri>) {
        val serviceIntent = Intent(
            this,
            TransferService::class.java
        ).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        ContextCompat.startForegroundService(this, serviceIntent)

        GlobalScope.launch(Dispatchers.IO) {
            val webSettingsRes = getWebServerSettings(applicationContext)
            val uploadedUrls = mutableListOf<String>()

            webSettingsRes
                .onSuccess { webSettings ->
                    val chunksEnabled = webSettings.config?.chunks?.enabled ?: true
                    val maxChunkSize = parseBytes(webSettings.config?.chunks?.max ?: "95mb")
                    val chunkSize = parseBytes(webSettings.config?.chunks?.size ?: "25mb")

                    uris.forEachIndexed { index, uri ->
                        val fileInfo = getFileInfo(applicationContext, uri) ?: return@forEachIndexed

                        val displayName = fileInfo.first
                        val tempFile = copyUriToTempFile(applicationContext, uri, displayName) ?: return@forEachIndexed
                        val fileExtension = displayName.substringAfterLast('.', "")

                        val title = "Uploading (${index + 1}/${uris.size})"

                        if (chunksEnabled && tempFile.length() >= maxChunkSize) {
                            uploadPartialFile(
                                context = applicationContext,
                                filePath = tempFile.absolutePath,
                                fileExtension = fileExtension,
                                chunkSize = chunkSize,
                                notificationTitle = title,
                                notificationContent = displayName
                            )
                                .onSuccess { response ->
                                    uploadedUrls.addAll(response.files.map { it.url })
                                }
                                .onFailure { error ->
                                    showToast("Upload ${index + 1} ($displayName) Failed: ${error.message}")
                                }
                        } else {
                            uploadFile(
                                context = applicationContext,
                                filePath = tempFile.absolutePath,
                                fileExtension = fileExtension,
                                notificationTitle = title,
                                notificationContent = displayName
                            )
                                .onSuccess { response ->
                                    uploadedUrls.addAll(response.files.map { it.url })
                                }
                                .onFailure { error ->
                                    showToast("Upload ${index + 1} ($displayName) Failed: ${error.message}")
                                }
                        }

                        tempFile.delete()
                    }

                    if (uploadedUrls.isNotEmpty()) {
                        copyToClipboardAndNotify(uploadedUrls.joinToString("\n"))
                    }
                }
                .onFailure { error ->
                    showToast("Upload Failed: ${error.message}")
                }

            finish()
        }
    }

    private suspend fun copyToClipboardAndNotify(text: String) {
        withContext(Dispatchers.Main) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Zipline", text)

            clipboard.setPrimaryClip(clip)

            Toast.makeText(applicationContext, "Links copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }
}