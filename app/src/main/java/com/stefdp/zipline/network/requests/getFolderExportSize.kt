package com.stefdp.zipline.network.requests

import android.content.Context
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[getFolderExportSize]"

suspend fun getFolderExportSize(
    context: Context,
    folderId: String,
): Result<Long> {
    try {
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

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).getFolderExportSize(
            token = token,
            folderId = folderId
        )

        return Result.success(response.headers()["Content-Length"]?.toLong() ?: -1L)
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}