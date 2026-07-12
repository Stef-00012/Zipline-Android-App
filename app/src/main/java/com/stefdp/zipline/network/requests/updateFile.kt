package com.stefdp.zipline.network.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.requests.UpdateFileBody
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[updateFile]"

suspend fun updateFile(
    context: Context,
    fileId: String,
    favorite: Boolean? = null,
    maxViews: Long? = null,
    password: String? = null,
    originalName: String? = null,
    type: String? = null,
    tags: List<String>? = null,
    name: String? = null,
): Result<File> {
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

        val requestBody = UpdateFileBody(
            favorite = favorite,
            maxViews = maxViews,
            password = password,
            originalName = originalName,
            type = type,
            tags = tags,
            name = name
        )

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).updateFile(
            token = token,
            fileId = fileId,
            data = requestBody
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
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                Logger.error(TAG, "Error message: ${json.error}")

                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )
        }

        if (body is File) {
            return Result.success(body)
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