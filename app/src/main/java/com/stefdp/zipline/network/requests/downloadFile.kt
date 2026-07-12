package com.stefdp.zipline.network.requests

import android.content.Context
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.transferservice.TransferServiceConnection
import com.stefdp.zipline.transferservice.util.copyStreamWithProgress
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CancellationException

suspend fun downloadFile(
    context: Context,
    fileId: String,
    password: String? = null,
    download: Boolean = true,
    destinationPath: String,
    notificationTitle: String = "Downloading file",
    notificationContent: String = "Download in progress",
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit = { _, _, _ -> },
): Result<String> {
    val secureStore = SecureStorage.getInstance(context)

    val serverUrl = secureStore.get(STORAGE_SERVER_URL_KEY)
    val token = secureStore.get(STORAGE_TOKEN_KEY)

    if (token.isNullOrEmpty()) {
        return Result.failure(
            Exception(context.getString(R.string.missing_token))
        )
    }

    if (serverUrl.isNullOrEmpty()) {
        return Result.failure(
            Exception(context.getString(R.string.missing_server_url))
        )
    }

    val service = TransferServiceConnection.getService(context)
    val transferId = service.registerTransfer(notificationTitle, notificationContent, onProgress)

    return try {
        val response = ZiplineApiClient.getZiplineApiService(serverUrl).downloadFile(token, fileId, password, download)

        if (!response.isSuccessful) {
            service.failTransfer(transferId, "HTTP ${response.code()}")

            return Result.failure(Exception("Download failed: HTTP ${response.code()}"))
        }

        val body = response.body() ?: run {
            service.failTransfer(transferId, "Empty response")

            return Result.failure(Exception("Download failed: empty response body"))
        }

        val totalBytes = body.contentLength()
        val file = File(destinationPath)

        file.parentFile?.mkdirs()

        body.byteStream().use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                copyStreamWithProgress(
                    inputStream = inputStream,
                    outputStream = outputStream,
                    totalBytes = totalBytes,
                    transferId = transferId,
                    service = service,
                    onProgress = onProgress,
                )
            }
        }

        service.completeTransfer(transferId, destinationPath)
        Result.success(destinationPath)
    } catch (e: CancellationException) {
        File(destinationPath).delete()

        Result.failure(Exception("Download cancelled"))
    } catch (e: Exception) {
        service.failTransfer(transferId, e.message ?: "Unknown error")
        File(destinationPath).delete()

        Result.failure(e)
    }
}