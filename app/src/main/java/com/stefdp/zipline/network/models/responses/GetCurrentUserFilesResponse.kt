package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.File

data class GetFilesResponse(
    val page: List<File>,
    val search: GetFilesResponseSearch? = null,
    val total: Long? = null,
    val pages: Long? = null,
)

data class GetFilesResponseSearch(
    val query: String,
    val field: String? = null,
)