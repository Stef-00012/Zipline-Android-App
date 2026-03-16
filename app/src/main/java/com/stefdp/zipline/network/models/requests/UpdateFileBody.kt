package com.stefdp.zipline.network.models.requests

data class UpdateFileBody(
    val favorite: Boolean? = null,
    val maxViews: Long? = null,
    val password: String? = null,
    val originalName: String? = null,
    val type: String? = null,
    val tags: List<String>? = null,
    val name: String? = null,
)
