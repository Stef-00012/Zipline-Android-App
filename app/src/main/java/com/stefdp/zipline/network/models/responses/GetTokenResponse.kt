package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.User

data class GetTokenResponse(
    val user: User? = null,
    val token: String? = null
)