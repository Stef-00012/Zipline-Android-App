package com.stefdp.zipline.network.models.responses

data class BulkUpdateFilesResponse(
    val count: Long,
     val name: String? = null, // only when adding to a folder
)
