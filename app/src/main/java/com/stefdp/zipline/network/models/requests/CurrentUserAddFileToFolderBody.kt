package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

data class AddFileToFolderBody(
    @SerializedName("id") val fileId: String,
)
