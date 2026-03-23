package com.stefdp.zipline.network.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.network.models.responses.UploadFileResponse
import com.stefdp.zipline.transferservice.ProgressTracker
import com.stefdp.zipline.transferservice.TransferServiceConnection
import com.stefdp.zipline.utils.SecureStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException

suspend fun uploadFile(
    context: Context,
    filePath: String,
    deletesAt: String? = null,
    format: FilesFormat? = null,
    imageCompressionPercent: Float? = null,
    imageCompressionType: UploadCompressionType? = null,
    password: String? = null,
    maxViews: Long? = null,
    originalName: String? = null,
    folder: String? = null,
    filename: String? = null,
    domain: String? = null,
    fileExtension: String? = null,
    fileMimeType: String? = null,
    notificationTitle: String = "Uploading file",
    notificationContent: String = "Upload in progress",
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit = { _, _, _ -> },
): Result<UploadFileResponse> {
    val secureStore = SecureStorage.getInstance(context)

    val serverUrl = secureStore.get("serverUrl")
    val token = secureStore.get("token")

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
    val transferId = service.registerTransfer(
        notificationTitle,
        notificationContent,
        onProgress
    )

    return try {
        val file = File(filePath)
        val totalBytes = file.length()
        val tracker = ProgressTracker()

        val mimeType = fileMimeType
            ?: java.net.URLConnection.guessContentTypeFromName(file.name)
            ?: "application/octet-stream"

        val requestBody = object : RequestBody() {
            override fun contentType() = mimeType.toMediaTypeOrNull()
            override fun contentLength() = totalBytes

            override fun writeTo(sink: BufferedSink) {
                try {
                    var bytesWritten = 0L
                    var lastNotifyTime = 0L

                    file.inputStream().source().use { source ->
                        val buffer = okio.Buffer()
                        var read: Long

                        while (source.read(buffer, 8192).also { read = it } != -1L) {
                            if (service.isTransferCancelled(transferId)) {
                                throw CancellationException("Upload cancelled")
                            }

                            sink.write(buffer, read)
                            bytesWritten += read

                            val now = System.currentTimeMillis()

                            if (now - lastNotifyTime >= 250) {
                                val speed = tracker.update(bytesWritten)
                                service.updateProgress(
                                    transferId,
                                    totalBytes,
                                    bytesWritten,
                                    speed
                                )

                                onProgress(
                                    totalBytes,
                                    bytesWritten,
                                    speed
                                )

                                lastNotifyTime = now
                            }
                        }
                    }

                    val speed = tracker.update(bytesWritten)

                    service.updateProgress(
                        transferId, totalBytes,
                        bytesWritten,
                        speed
                    )

                    onProgress(
                        totalBytes,
                        bytesWritten,
                        speed
                    )

                } catch(e: CancellationException) {
                    throw IOException("Upload cancelled", e)
                }
            }
        }

        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).uploadFile(
            token = token,
            deletesAt = deletesAt,
            format = format,
            imageCompressionPercent = imageCompressionPercent,
            imageCompressionType = imageCompressionType,
            password = password,
            maxViews = maxViews,
            originalName = originalName,
            folder = folder,
            filename = filename,
            domain = domain,
            fileExtension = fileExtension,
            file = part,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            if (statusCode == 401) {
                return Result.failure(
                    Exception(context.getString(R.string.invalid_token))
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            service.failTransfer(transferId, "HTTP ${response.code()}")

            if (json.error.isNotEmpty()) {
                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )
        }

        if (body is UploadFileResponse) {
            service.completeTransfer(transferId)

            Result.success(body)
        } else {
            service.failTransfer(transferId, context.getString(R.string.generic_error))

            Result.failure(Exception(context.getString(R.string.generic_error)))
        }
    } catch (e: CancellationException) {
        Result.failure(Exception("Upload cancelled"))
    } catch (e: Exception) {
        service.failTransfer(transferId, e.message ?: "Unknown error")

        Result.failure(e)
    }
}