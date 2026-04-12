package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.CoreSettings
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.UpdateServerSettingsResult
import com.stefdp.zipline.network.models.responses.UpdateServerSettingsErrorResponse
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[updateCoreSettings]"

suspend fun updateCoreSettings(
    context: Context,
    data: CoreSettings
): Result<UpdateServerSettingsResult> {
    try {
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

        val response = ZiplineApiClient.getZiplineApiService(
            baseUrl = serverUrl,
            includeNull = true
        ).updateCoreSettings(
            token = token,
            data = data
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Logger.error(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception(context.getString(R.string.invalid_token))
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, UpdateServerSettingsErrorResponse::class.java)

            Logger.error(TAG, "Error: $json")

            return Result.success(
                UpdateServerSettingsResult.Error(json)
            )
        }

        if (body is ServerSettings) {
            return Result.success(
                UpdateServerSettingsResult.Success(body)
            )
        }

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}