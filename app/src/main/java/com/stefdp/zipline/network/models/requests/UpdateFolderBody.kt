package com.stefdp.zipline.network.models.requests

data class UpdateFolderBody(
    val isPublic: Boolean? = null,
    val name: String? = null,
    val allowUploads: Boolean? = null,
)