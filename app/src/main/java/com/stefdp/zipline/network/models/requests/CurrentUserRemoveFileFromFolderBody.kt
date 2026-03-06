package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.network.models.DeleteFolderType

data class RemoveFileFromFolderBody(
    val delete: String = DeleteFolderType.FILE.toString(),
    @SerializedName("id") val fileId: String,
)
