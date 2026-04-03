package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.Folder
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.Url
import com.stefdp.zipline.network.models.requests.CreateTagBody
import com.stefdp.zipline.network.models.requests.CreateUrlBody
import com.stefdp.zipline.network.models.responses.CreateUrlResponse
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[getFolders]"

suspend fun getFolders(
    context: Context,
    userId: String? = null,
    parent: String? = null,
    root: Boolean? = null,
    excludeFiles: Boolean? = null,
): Result<List<BaseFolder>> {
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

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).getFolders(
            token = token,
            user = userId,
            parent = parent,
            root = root,
            excludeFiles = excludeFiles
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

        if (body is List<BaseFolder>) {
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