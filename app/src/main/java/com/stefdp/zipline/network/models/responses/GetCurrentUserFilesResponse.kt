package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.File

data class GetCurrentUserFilesResponse(
    val page: List<File>,
    val search: GetCurrentUserFilesResponseSearch? = null,
    val total: Long? = null,
    val pages: Long? = null,
)

data class GetCurrentUserFilesResponseSearch(
    val query: String,
    val field: String? = null,
)