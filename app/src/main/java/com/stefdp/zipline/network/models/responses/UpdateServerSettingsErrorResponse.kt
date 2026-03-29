package com.stefdp.zipline.network.models.responses

data class UpdateServerSettingsErrorResponse(
    override val error: String,
    override val statusCode: Int,
    override val message: String? = null,
    override val code: Int? = null,
    val issues: List<UpdateServerSettingsErrorIssue>? = null,
) : BaseErrorResponse

data class UpdateServerSettingsErrorIssue(
    val code: String,
    val message: String,
    val path: List<String>,
)