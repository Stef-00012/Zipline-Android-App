package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.Folder
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.UserRole
import com.stefdp.zipline.network.models.requests.AddFileToFolderBody
import com.stefdp.zipline.network.models.requests.CreateUserBody
import com.stefdp.zipline.network.models.requests.DeleteUserBody
import com.stefdp.zipline.network.models.requests.UpdateFileBody
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[deleteUser]"

suspend fun deleteUser(
    context: Context,
    userId: String,
    deleteUserFilesAndUrls: Boolean? = null
): Result<User> {
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

        val requestBody = DeleteUserBody(
            deleteUserFilesAndUrls = deleteUserFilesAndUrls
        )

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).deleteUser(
            token = token,
            userId = userId,
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

        if (body is User) {
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