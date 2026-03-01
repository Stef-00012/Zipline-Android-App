package com.stefdp.zipline.network.models.responses

data class BulkUpdateCurrentUserFilesResponse(
    val count: Long,
     val name: String? = null,
)
