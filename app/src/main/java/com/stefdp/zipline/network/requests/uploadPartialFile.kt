package com.stefdp.zipline.network.requests


import android.content.Context
import com.google.gson.Gson
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.network.models.responses.UploadPartialFileResponse
import com.stefdp.zipline.transferservice.ProgressTracker
import com.stefdp.zipline.transferservice.TransferServiceConnection
import com.stefdp.zipline.utils.SecureStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.CancellationException

suspend fun uploadPartialFile(
    context: Context,
    filePath: String,
    chunkSize: Long = 25 * 1024 * 1024,
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
    notificationTitle: String = "Uploading file (chunked)",
    notificationContent: String = "Upload in progress",
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit = { _, _, _ -> },
): Result<UploadPartialFileResponse> {
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
    val transferId = service.registerTransfer(notificationTitle, notificationContent, onProgress)

    return try {
        val file = File(filePath)
        val totalFileSize = file.length()

        val mimeType = fileMimeType
            ?: java.net.URLConnection.guessContentTypeFromName(file.name)
            ?: "application/octet-stream"

        var offset = 0L
        var identifier: String? = null
        var lastResponse: UploadPartialFileResponse? = null
        val tracker = ProgressTracker()

        while (offset < totalFileSize) {
            if (service.isTransferCancelled(transferId)) {
                throw CancellationException("Upload cancelled")
            }

            val remaining = totalFileSize - offset
            val currentChunkSize = minOf(chunkSize, remaining)
            val isLastChunk = (offset + currentChunkSize) >= totalFileSize
            val currentOffset = offset

            val rangeEnd = minOf(currentOffset + chunkSize, totalFileSize) - 1
            val contentRange = "bytes $currentOffset-$rangeEnd/$totalFileSize"

            val requestBody = object : RequestBody() {
                override fun contentType() = mimeType.toMediaTypeOrNull()
                override fun contentLength() = currentChunkSize

                override fun writeTo(sink: BufferedSink) {
                    try {
                        val raf = RandomAccessFile(file, "r")

                        raf.use {
                            it.seek(currentOffset)
                            val buffer = ByteArray(8192)
                            var bytesRemaining = currentChunkSize
                            var lastNotifyTime = 0L

                            while (bytesRemaining > 0) {
                                if (service.isTransferCancelled(transferId)) {
                                    throw CancellationException("Upload cancelled")
                                }

                                val toRead = minOf(buffer.size.toLong(), bytesRemaining).toInt()
                                val read = it.read(buffer, 0, toRead)

                                if (read == -1) break

                                sink.write(buffer, 0, read)
                                bytesRemaining -= read

                                val totalBytesUploaded = currentOffset + (currentChunkSize - bytesRemaining)
                                val now = System.currentTimeMillis()

                                if (now - lastNotifyTime >= 250) {
                                    val speed = tracker.update(totalBytesUploaded)

                                    service.updateProgress(
                                        transferId,
                                        totalFileSize,
                                        totalBytesUploaded,
                                        speed
                                    )

                                    onProgress(
                                        totalFileSize,
                                        totalBytesUploaded,
                                        speed
                                    )

                                    lastNotifyTime = now
                                }
                            }
                        }
                    } catch(e: CancellationException) {
                        throw IOException("Upload cancelled", e)
                    }
                }
            }

            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

            val response = ZiplineApiClient.getZiplineApiService(serverUrl).uploadPartialFile(
                token = token,
                partialFilename = file.name,
                partialContentType = mimeType,
                isLastChunk = isLastChunk,
                partialContentLength = currentChunkSize,
                identifier = identifier,
                contentRange = contentRange,
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

                return Result.failure(Exception("Chunked upload failed at offset $offset: HTTP ${response.code()}"))
            }

            lastResponse = body

            if (identifier == null && body is UploadPartialFileResponse) {
                identifier = body.partialIdentifier
            }

            offset += currentChunkSize

            val speed = tracker.update(offset)

            service.updateProgress(
                transferId,
                totalFileSize,
                offset,
                speed
            )

            onProgress(
                totalFileSize,
                offset,
                speed
            )
        }

        if (lastResponse == null) {
            service.failTransfer(transferId, "No response received")

            return Result.failure(Exception("Chunked upload failed: no response received"))
        }

        service.completeTransfer(transferId)
        Result.success(lastResponse)
    } catch (e: CancellationException) {
        Result.failure(Exception("Upload cancelled"))
    } catch (e: Exception) {
        service.failTransfer(transferId, e.message ?: "Unknown error")

        Result.failure(e)
    }
}