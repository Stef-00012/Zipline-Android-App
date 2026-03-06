package com.stefdp.zipline.network.models.responses

data class ErrorResponse(
    val error: String,
    val statusCode: Int
)