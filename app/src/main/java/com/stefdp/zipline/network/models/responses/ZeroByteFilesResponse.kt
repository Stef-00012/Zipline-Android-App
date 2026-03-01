package com.stefdp.zipline.network.models.responses

data class ZeroByteFilesResponse(
    val status: String? = null,
    val files: List<ZeroByteFilesResponseFiles>? = null
)

data class ZeroByteFilesResponseFiles(
    val name: String,
    val id: String
)
