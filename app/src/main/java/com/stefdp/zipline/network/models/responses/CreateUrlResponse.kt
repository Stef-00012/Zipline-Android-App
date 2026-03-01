package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.BaseUrl

data class CreateUrlResponse(
    override val similarity: Double? = null,
    override val id: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val code: String,
    override val vanity: String? = null,
    override val destination: String,
    override val views: Long,
    override val maxViews: Long? = null,
    override val password: String? = null,
    override val enabled: Boolean,
    override val userId: String,
    val url: String
) : BaseUrl