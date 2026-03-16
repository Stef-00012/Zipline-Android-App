package com.stefdp.zipline.network.models.responses

import com.google.gson.annotations.JsonAdapter
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.utils.StringOrListDeserializer

data class GetFilesResponse(
    val page: List<File>,
    val search: GetFilesResponseSearch? = null,
    val total: Long? = null,
    val pages: Long? = null,
)

data class GetFilesResponseSearch(
    @JsonAdapter(StringOrListDeserializer::class)
    val query: List<String>? = null,
    val field: String? = null,
)