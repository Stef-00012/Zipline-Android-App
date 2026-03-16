package com.stefdp.zipline.network.models.responses

import com.stefdp.zipline.network.models.UserSession

data class GetSessionsResponse(
    val current: UserSession,
    val other: List<UserSession>
)
