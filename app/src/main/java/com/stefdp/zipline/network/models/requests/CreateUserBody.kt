package com.stefdp.zipline.network.models.requests

import com.stefdp.zipline.network.models.UserRole

data class CreateUserBody(
    val username: String,
    val password: String,
    val avatar: String? = null,
    val role: UserRole? = null,
)
