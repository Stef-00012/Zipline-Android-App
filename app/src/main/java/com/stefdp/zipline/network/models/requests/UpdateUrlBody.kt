package com.stefdp.zipline.network.models.requests

data class UpdateUrlBody(
    val password: String? = null,
    val vanity: String? = null,
    val maxViews: Long? = null,
    val destination: String? = null,
    val enabled: Boolean? = null,
)
