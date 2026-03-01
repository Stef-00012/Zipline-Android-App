package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.User

data class LoginResponse(
    val totp: Boolean? = null,
    val user: User? = null
)
