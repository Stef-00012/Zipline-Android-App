package com.stefdp.zipline.network.models.requests

data class BulkUpdateCurrentUserFilesBody(
    val files: List<String>,
    val favorite: Boolean? = null,
    val folder: String? = null,
)
