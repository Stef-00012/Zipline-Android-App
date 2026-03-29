package com.stefdp.zipline.network.models.responses

interface BaseErrorResponse {
    val error: String
    val statusCode: Int
    val message: String?
    val code: Int?
}

data class ErrorResponse(
    override val error: String,
    override val statusCode: Int,
    override val message: String? = null,
    override val code: Int? = null,
) : BaseErrorResponse