package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

data class DeleteUserBody(
    @SerializedName("delete") val deleteUserFilesAndUrls: Boolean? = null,
)
