package com.stefdp.zipline.network.models.requests

data class CreateInviteBody(
    val expiresAt: String? = null,
    val maxUses: Long? = null,
)
