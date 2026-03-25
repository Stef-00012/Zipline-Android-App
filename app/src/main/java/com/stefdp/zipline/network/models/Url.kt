package com.stefdp.zipline.network.models

interface BaseUrl {
    val similarity: Double?
    val id: String
    val createdAt: String
    val updatedAt: String
    val code: String
    val vanity: String?
    val destination: String
    val views: Long
    val maxViews: Long?
    val enabled: Boolean
    val userId: String
}

data class Url(
    override val similarity: Double? = null,
    override val id: String,
    override val createdAt: String,
    override val updatedAt: String,
    override val code: String,
    override val vanity: String? = null,
    override val destination: String,
    override val views: Long,
    override val maxViews: Long? = null,
    override val enabled: Boolean,
    override val userId: String,
) : BaseUrl
