package com.stefdp.zipline.network.models.requests

data class CreateFolderBody(
    val name: String,
    val isPublic: Boolean? = null,
    val files: List<String>? = null,
    val parentId: String? = null,
)
