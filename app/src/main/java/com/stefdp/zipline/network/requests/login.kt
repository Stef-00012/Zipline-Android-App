package com.stefdp.zipline.network.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.requests.LoginBody
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.network.models.responses.LoginResponse
import com.stefdp.zipline.utils.SecureStorage

private const val TAG = "ZiplineApi[login]"

suspend fun login(
    context: Context,
    username: String,
    password: String,
    code: String? = null
): Result<LoginResult> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val serverUrl = secureStore.get("serverUrl")

        if (serverUrl.isNullOrEmpty()) {
            return Result.failure(
                Exception(context.getString(R.string.missing_server_url))
            )
        }

        val requestBody = LoginBody(
            username,
            password,
            code
        )

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).login(
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

        if (body is LoginResponse) {
            if (body.totp == true) {
                return Result.success(
                    LoginResult.TotpRequired
                )
            }

            val headers = response.headers()
            val setCookieHeader = headers["Set-Cookie"] ?: return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )

            val authCookie = setCookieHeader.split(";").firstOrNull() ?: return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )

            return Result.success(
                LoginResult.Success(authCookie)
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

sealed interface LoginResult {
    data class Success(val authCookie: String) : LoginResult
    data object TotpRequired : LoginResult
}