package com.stefdp.zipline.network.models

data class Invite(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val expiresAt: String? = null,
    val code: String,
    val uses: Long,
    val maxUses: Long? = null,
    val inviterId: String,
    val inviter: InviteInviter
)

data class InviteInviter(
    val id: String,
    val username: String,
    val role: UserRole
)
