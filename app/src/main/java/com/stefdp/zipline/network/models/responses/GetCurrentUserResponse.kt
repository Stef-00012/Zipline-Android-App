package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.User

data class GetCurrentUserResponse(
    val user: User? = null
)