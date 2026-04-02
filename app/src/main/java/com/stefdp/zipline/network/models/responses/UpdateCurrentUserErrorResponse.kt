package com.stefdp.zipline.network.models.responses

data class UpdateCurrentUserErrorResponse(
    override val error: String,
    override val statusCode: Int,
    override val message: String? = null,
    override val code: Int? = null,
    val issues: List<UpdateCurrentUserErrorIssue>? = null,
) : BaseErrorResponse

data class UpdateCurrentUserErrorIssue(
    val instancePath: String,
    val keyword: String,
    val message: String,
    val params: UpdateCurrentUserErrorIssueParams,
    val schemaPath: String
)

data class UpdateCurrentUserErrorIssueParams(
    val origin: String,
    val minimum: Long,
    val inclusive: Boolean
)