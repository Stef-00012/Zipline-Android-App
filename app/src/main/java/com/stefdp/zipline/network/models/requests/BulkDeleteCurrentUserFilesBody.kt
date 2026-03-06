package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

data class BulkDeleteFilesBody(
    val files: List<String>,
    @SerializedName("delete_datasourceFiles") val deleteDatasourceFiles: Boolean? = null,
)
