package com.stefdp.zipline.network.models.responses

data class UploadPartialFileResponse(
    val files: List<UploadPartialFileResponseFile>,
    val deletesAt: String? = null,
    val assumedMimetypes: List<Boolean>? = null,
    val partialSuccess: Boolean? = null,
    val partialIdentifier: String? = null,
)

data class UploadPartialFileResponseFile(
    val id: String,
    val type: String,
    val url: String,
    val pending: Boolean? = true,
)
