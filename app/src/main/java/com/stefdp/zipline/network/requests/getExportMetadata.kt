package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.network.models.responses.LoginResponse
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[getExportSize]"

suspend fun getExportSize(
    context: Context,
    excludeMetrics: Boolean? = null
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

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).getExportSize(
            token = token,
            noMetrics = excludeMetrics
        )

        return Result.success(response.headers()["Content-Length"]?.toLong() ?: -1L)
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}