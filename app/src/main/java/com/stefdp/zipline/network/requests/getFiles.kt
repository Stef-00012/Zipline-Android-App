package com.stefdp.zipline.network.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.zipline.Logger
import com.stefdp.zipline.R
import com.stefdp.zipline.network.ZiplineApiClient
import com.stefdp.zipline.network.models.requests.GetFilesQueryFilter
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.network.models.responses.ErrorResponse
import com.stefdp.zipline.network.models.responses.GetFilesResponse
import com.stefdp.zipline.utils.STORAGE_SERVER_URL_KEY
import com.stefdp.zipline.utils.STORAGE_TOKEN_KEY
import com.stefdp.zipline.utils.SecureStorage
import com.stefdp.zipline.utils.SortOrder

private const val TAG = "ZiplineApi[getFiles]"

suspend fun getFiles(
    context: Context,
    page: Long = 1,
    perPage: Long? = null,
    filter: GetFilesQueryFilter? = null,
    filterFavorite: Boolean? = null,
    sortBy: GetFilesQuerySortBy? = null,
    sortOrder: SortOrder? = null,
    searchField: GetFilesQuerySearchField? = null,
    searchQuery: String? = null,
    userId: String? = null,
    folderId: String? = null
): Result<GetFilesResponse> {
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

        val response = ZiplineApiClient.getZiplineApiService(serverUrl).getFiles(
            token = token,
            page = page,
            perPage = perPage,
            filter = filter,
            filterFavorite = filterFavorite,
            sortBy = sortBy,
            sortOrder = sortOrder,
            searchField = searchField,
            searchQuery = searchQuery,
            userId = userId,
            folderId = folderId
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

        if (body is GetFilesResponse) {
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