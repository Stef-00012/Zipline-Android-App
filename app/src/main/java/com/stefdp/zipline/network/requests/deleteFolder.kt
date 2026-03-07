package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.Folder
import com.stefdp.zipline.network.models.requests.AddFileToFolderBody
import com.stefdp.zipline.network.models.requests.DeleteFolderBody
import com.stefdp.zipline.network.models.requests.RemoveFileFromFolderBody
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[deleteFolder]"

suspend fun deleteFolder(
    context: Context,
    folderId: String,
): Result<BaseFolder> {
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

        val requestBody = DeleteFolderBody()

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).deleteFolder(
            token = token,
            folderId = folderId,
            data = requestBody

        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception(context.getString(R.string.invalid_token))
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                Log.e(TAG, "Error message: ${json.error}")

                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )
        }

        if (body is Folder) {
            return Result.success(body)
        }

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}