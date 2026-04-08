package com.stefdp.zipline.network.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.requests.RemoveCurrentUserAvatarBody
import com.stefdp.zipline.network.models.responses.GetCurrentUserResponse
import com.stefdp.zipline.network.models.responses.UpdateCurrentUserErrorResponse
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[updateCurrentUser]"

suspend fun removeCurrentUserAvatar(
    context: Context,
): Result<UpdateCurrentUserResult> {
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
        ).removeCurrentUserAvatar(
            token = token,
            data = RemoveCurrentUserAvatarBody()
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
            val json = Gson().fromJson(errorBody, UpdateCurrentUserErrorResponse::class.java)

            return Result.success(
                UpdateCurrentUserResult.Error(json)
            )
        }

        if (body is GetCurrentUserResponse) {
            return Result.success(
                UpdateCurrentUserResult.Success(body)
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