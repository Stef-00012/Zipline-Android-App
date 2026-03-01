package com.stefdp.zipline.network.models.responses

data class UploadFileResponse(
    val files: List<UploadFileResponseFile>,
    val deletesAt: String? = null,
    val assumedMimetypes: List<Boolean>? = null,
)

data class UploadFileResponseFile(
    val id: String,
    val type: String,
    val url: String,
    val pending: Boolean? = null,
)
