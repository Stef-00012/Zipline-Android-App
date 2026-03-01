package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

data class BulkDeleteCurrentUserFilesBody(
    val files: List<String>,
    @SerializedName("delete_datasourceFiles") val deleteDatasourceFiles: Boolean? = null,
)
