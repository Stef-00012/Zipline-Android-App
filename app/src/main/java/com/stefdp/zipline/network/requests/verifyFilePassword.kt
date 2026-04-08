package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.Folder
import com.stefdp.zipline.network.models.requests.AddFileToFolderBody
import com.stefdp.zipline.network.models.requests.UpdateFileBody
import com.stefdp.zipline.network.models.requests.VerifyFilePasswordBody
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.network.models.responses.VerifyFilePasswordResponse
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[verifyFilePassword]"

suspend fun verifyFilePassword(
    context: Context,
    fileId: String,
    password: String
): Result<Boolean> {
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

        val requestBody = VerifyFilePasswordBody(
            password = password
        )

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).verifyFilePassword(
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

        if (body is VerifyFilePasswordResponse) {
            return Result.success(body.success)
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