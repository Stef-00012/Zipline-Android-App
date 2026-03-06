package com.stefdp.zipline.network.models.requests

data class DeleteSessionBody(
    val sessionId: String? = null,
    val all: Boolean? = null,
)
