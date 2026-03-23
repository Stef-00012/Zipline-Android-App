package com.stefdp.zipline.transferservice.util

import com.stefdp.zipline.transferservice.ProgressTracker
import com.stefdp.zipline.transferservice.TransferService
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CancellationException

suspend fun copyStreamWithProgress(
    inputStream: InputStream,
    outputStream: OutputStream,
    totalBytes: Long,
    transferId: String,
    service: TransferService,
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit,
) {
    val tracker = ProgressTracker()
    val buffer = ByteArray(8192)
    var bytesRead: Int
    var totalRead = 0L
    var lastNotifyTime = 0L

    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        if (service.isTransferCancelled(transferId)) {
            throw CancellationException("Transfer cancelled")
        }

        outputStream.write(buffer, 0, bytesRead)

        totalRead += bytesRead

        val now = System.currentTimeMillis()

        if (now - lastNotifyTime >= 250) {
            val speed = tracker.update(totalRead)

            service.updateProgress(transferId, totalBytes, totalRead, speed)
            onProgress(totalBytes, totalRead, speed)

            lastNotifyTime = now
        }
    }

    val speed = tracker.update(totalRead)

    service.updateProgress(transferId, totalBytes, totalRead, speed)
    onProgress(totalBytes, totalRead, speed)
}