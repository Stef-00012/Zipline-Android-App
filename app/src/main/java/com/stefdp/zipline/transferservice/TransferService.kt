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
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TransferService : Service() {

    companion object {
        const val CHANNEL_ID = "zipline_transfer_channel"
        const val ACTION_CANCEL = "com.stefdp.zipline.ACTION_CANCEL_TRANSFER"
        const val EXTRA_TRANSFER_ID = "transfer_id"
        private const val ONGOING_NOTIFICATION_ID_BASE = 10000
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
        // Start as foreground with a silent initial notification only if needed
        // This will be replaced immediately by the real transfer notification
        if (activeTransfers.isEmpty()) {
            val notification = buildPlaceholderNotification()
//            startForeground(ONGOING_NOTIFICATION_ID_BASE, notification)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    ONGOING_NOTIFICATION_ID_BASE,
                    notification,
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(
                    ONGOING_NOTIFICATION_ID_BASE,
                    notification
                )
            }
        }
        return START_NOT_STICKY
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
//            startForeground(notificationId, notification)
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
            } catch (_: Exception) {
                // Ignore if we can't create the intent
            }
        }

        return builder.build()
    }
}