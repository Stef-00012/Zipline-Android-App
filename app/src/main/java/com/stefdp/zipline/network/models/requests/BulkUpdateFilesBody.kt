package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

data class BulkUpdateFilesBody(
    val files: List<String>,
    val favorite: Boolean? = null,
    @SerializedName("folder") val folderId: String? = null,
)
