package com.stefdp.zipline.network.models.requests

data class DeleteCurrentUserSessionBody(
    val sessionId: String? = null,
    val all: Boolean? = null,
)
