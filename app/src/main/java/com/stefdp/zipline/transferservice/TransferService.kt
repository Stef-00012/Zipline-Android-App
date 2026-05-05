package com.stefdp.zipline.transferservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.stefdp.zipline.R
import com.stefdp.zipline.utils.formatBytes
import com.stefdp.zipline.utils.formatSpeed
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import com.stefdp.zipline.network.requests.createUrl
import com.stefdp.zipline.network.requests.getWebServerSettings
import com.stefdp.zipline.network.requests.uploadFile
import com.stefdp.zipline.network.requests.uploadPartialFile
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.STORAGE_DEFAULT_DOMAIN_KEY
import com.stefdp.zipline.utils.parseBytes
import kotlinx.coroutines.*
import java.net.URI
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransferService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "zipline_transfer_channel"
        const val ACTION_CANCEL = "com.stefdp.zipline.ACTION_CANCEL_TRANSFER"
        const val EXTRA_TRANSFER_ID = "transfer_id"
        private const val ONGOING_NOTIFICATION_ID_BASE = 10000

        const val ACTION_QUICK_SHARE = "com.stefdp.zipline.ACTION_QUICK_SHARE"
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_FILE_PATHS = "EXTRA_FILE_PATHS"
    }

    private val binder = TransferBinder()
    private val activeTransfers = ConcurrentHashMap<String, TransferInfo>()
    private var notificationIdCounter = ONGOING_NOTIFICATION_ID_BASE

    private val cancelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_CANCEL) {
                val transferId = intent.getStringExtra(EXTRA_TRANSFER_ID) ?: return
                cancelTransfer(transferId)
            }
        }
    }

    data class TransferInfo(
        val id: String,
        val notificationId: Int,
        val title: String,
        val content: String,
        var totalBytes: Long = 0L,
        var bytesTransferred: Long = 0L,
        var speedBytesPerSecond: Double = 0.0,
        var isCancelled: Boolean = false,
        var isComplete: Boolean = false,
        var resultFilePath: String? = null,
        var onProgress: ((totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit)? = null,
    )

    inner class TransferBinder : Binder() {
        fun getService(): TransferService = this@TransferService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        ContextCompat.registerReceiver(
            this,
            cancelReceiver,
            IntentFilter(ACTION_CANCEL),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(cancelReceiver)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildPlaceholderNotification()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(ONGOING_NOTIFICATION_ID_BASE, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(ONGOING_NOTIFICATION_ID_BASE, notification)
        }

        val text = intent?.getStringExtra(EXTRA_TEXT)
        val filePaths = intent?.getStringArrayListExtra(EXTRA_FILE_PATHS)

        if (text != null || !filePaths.isNullOrEmpty()) {
            processQuickShare(text, filePaths)
        }

        return START_NOT_STICKY
    }

    private fun processQuickShare(text: String?, filePaths: List<String>?) {
        serviceScope.launch {
            try {
                if (text != null && text.startsWith("http", ignoreCase = true)) {
                    handleUrlShortening(text)
                }

                if (!filePaths.isNullOrEmpty()) {
                    handleFileUploads(filePaths)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    stopSelfIfIdle()
                }
            }
        }
    }

    private suspend fun handleUrlShortening(url: String) {
        val transferId = registerTransfer("Shortening URL", url)
        val secureStore = SecureStorage.getInstance(applicationContext)
        val defaultDomain = secureStore.get(STORAGE_DEFAULT_DOMAIN_KEY)

        createUrl(
            context = applicationContext,
            destination = url,
            domain = defaultDomain?.let { URI(it).host ?: it }
        ).onSuccess {
            completeTransfer(transferId)
            copyToClipboardAndNotify(it.url)
        }.onFailure { error ->
            failTransfer(transferId, error.message ?: "Unknown Error")
        }
    }

    private suspend fun handleFileUploads(filePaths: List<String>) {
        val webSettingsRes = getWebServerSettings(applicationContext)
        val uploadedUrls = mutableListOf<String>()

        webSettingsRes.onSuccess { webSettings ->
            val chunksEnabled = webSettings.config?.chunks?.enabled ?: true
            val maxChunkSize = parseBytes(webSettings.config?.chunks?.max ?: "95mb")
            val chunkSize = parseBytes(webSettings.config?.chunks?.size ?: "25mb")

            val secureStore = SecureStorage.getInstance(applicationContext)
            val defaultDomain = secureStore.get(STORAGE_DEFAULT_DOMAIN_KEY)
            val domainHost = defaultDomain?.let { URI(it).host ?: it }

            filePaths.forEachIndexed { index, filePath ->
                val tempFile = File(filePath)
                val displayName = tempFile.name
                val transferId = registerTransfer("Uploading (${index + 1}/${filePaths.size})", displayName)
                val fileExtension = displayName.substringAfterLast('.', "")
                val title = "Uploading (${index + 1}/${filePaths.size})"

                if (chunksEnabled && tempFile.length() >= maxChunkSize) {
                    uploadPartialFile(
                        context = applicationContext,
                        filePath = filePath,
                        fileExtension = fileExtension,
                        chunkSize = chunkSize,
                        notificationTitle = title,
                        notificationContent = displayName,
                        domain = domainHost
                    ).onSuccess { response ->
                        uploadedUrls.addAll(response.files.map { it.url })
                        completeTransfer(transferId)
                    }.onFailure { error ->
                        failTransfer(transferId, error.message ?: "Partial upload failed")
                    }
                } else {
                    uploadFile(
                        context = applicationContext,
                        filePath = filePath,
                        fileExtension = fileExtension,
                        notificationTitle = title,
                        notificationContent = displayName,
                        domain = domainHost
                    ).onSuccess { response ->
                        uploadedUrls.addAll(response.files.map { it.url })
                        completeTransfer(transferId)
                    }.onFailure { error ->
                        failTransfer(transferId, error.message ?: "Standard upload failed")
                    }
                }

                tempFile.delete()
            }

            if (uploadedUrls.isNotEmpty()) {
                copyToClipboardAndNotify(uploadedUrls.joinToString("\n"))
            }
        }.onFailure { error ->
            showToast("Server Settings Error: ${error.message}")
        }
    }

    private suspend fun copyToClipboardAndNotify(text: String) {
        withContext(Dispatchers.Main) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Zipline", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(applicationContext, "Links copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "File Transfers",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows progress of file uploads and downloads"
            setSound(null, null)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildPlaceholderNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Transfer Service")
            .setContentText("Preparing...")
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun registerTransfer(
        title: String,
        content: String,
        onProgress: ((totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit)? = null,
    ): String {
        val transferId = UUID.randomUUID().toString()
        val notificationId = notificationIdCounter++

        val info = TransferInfo(
            id = transferId,
            notificationId = notificationId,
            title = title,
            content = content,
            onProgress = onProgress,
        )

        activeTransfers[transferId] = info

        val notification = buildProgressNotification(info)

        if (activeTransfers.size == 1) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    notificationId,
                    notification,
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(
                    notificationId,
                    notification
                )
            }
        } else {
            val manager = getSystemService(NotificationManager::class.java)
            manager.notify(notificationId, notification)
        }

        return transferId
    }

    fun updateProgress(
        transferId: String,
        totalBytes: Long,
        bytesTransferred: Long,
        speedBytesPerSecond: Double,
    ) {
        val info = activeTransfers[transferId] ?: return
        info.totalBytes = totalBytes
        info.bytesTransferred = bytesTransferred
        info.speedBytesPerSecond = speedBytesPerSecond

        info.onProgress?.invoke(totalBytes, bytesTransferred, speedBytesPerSecond)

        val notification = buildProgressNotification(info)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(info.notificationId, notification)
    }

    fun completeTransfer(transferId: String, filePath: String? = null) {
        val info = activeTransfers[transferId] ?: return
        info.isComplete = true
        info.resultFilePath = filePath

        val notification = buildCompleteNotification(info)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(info.notificationId, notification)

        activeTransfers.remove(transferId)
        stopSelfIfIdle()
    }

    fun failTransfer(transferId: String, errorMessage: String) {
        val info = activeTransfers[transferId] ?: return

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(info.title)
            .setContentText("Failed: $errorMessage")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(info.notificationId, notification)

        activeTransfers.remove(transferId)
        stopSelfIfIdle()
    }

    fun cancelTransfer(transferId: String) {
        val info = activeTransfers[transferId] ?: return
        info.isCancelled = true

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(info.title)
            .setContentText("Cancelled")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(info.notificationId, notification)

        activeTransfers.remove(transferId)
        stopSelfIfIdle()
    }

    fun isTransferCancelled(transferId: String): Boolean {
        return activeTransfers[transferId]?.isCancelled ?: true
    }

    private fun stopSelfIfIdle() {
        if (activeTransfers.isEmpty()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun buildProgressNotification(info: TransferInfo): Notification {
        val cancelIntent = Intent(ACTION_CANCEL).apply {
            putExtra(EXTRA_TRANSFER_ID, info.id)
            setPackage(packageName)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            info.notificationId,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val progress = if (info.totalBytes > 0) {
            ((info.bytesTransferred * 100) / info.totalBytes).toInt()
        } else {
            0
        }

        val speedText = formatSpeed(info.speedBytesPerSecond)
        val contentText = if (info.totalBytes > 0) {
            "${info.content} • $progress% • $speedText"
        } else {
            "${info.content} • ${formatBytes(info.bytesTransferred)} • $speedText"
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(info.title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Cancel",
                cancelPendingIntent
            )

        if (info.totalBytes > 0) {
            builder.setProgress(100, progress, false)
        } else {
            builder.setProgress(0, 0, true)
        }

        return builder.build()
    }

    private fun buildCompleteNotification(info: TransferInfo): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(info.title)
            .setContentText("Complete")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)

        info.resultFilePath?.let { path ->
            try {
                val file = File(path)
                val uri: Uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                val mimeType = contentResolver.getType(uri) ?: "*/*"
                val openIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val openPendingIntent = PendingIntent.getActivity(
                    this,
                    info.notificationId,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.setContentIntent(openPendingIntent)
            } catch (_: Exception) {}
        }

        return builder.build()
    }
}