package com.stefdp.zipline.network.models.requests

import com.stefdp.zipline.network.models.UserViewSettings

data class UpdateCurrentUserBody(
    val username: String? = null,
    val password: String? = null,
    val avatar: String? = null,
    val view: UserViewSettings? = null
)
